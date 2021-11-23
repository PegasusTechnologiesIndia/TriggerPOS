package org.phomellolitepos.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.OperationApplicationException;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.phomellolitepos.Adapter.DialogOrderTypeListAdapter;
import org.phomellolitepos.Mail.GMailSender;
import org.phomellolitepos.MainActivity;
import org.phomellolitepos.ParkingIndustryActivity;
import org.phomellolitepos.PaymentActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.SetttingsActivity;
import org.phomellolitepos.Util.DateUtill;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Address;
import org.phomellolitepos.database.Address_Lookup;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Contact_Bussiness_Group;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Last_Code;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.OrderTaxArray;
import org.phomellolitepos.database.Order_Detail;
import org.phomellolitepos.database.Order_Detail_Tax;
import org.phomellolitepos.database.Order_Item_Tax;
import org.phomellolitepos.database.Order_Payment;
import org.phomellolitepos.database.Order_Type;
import org.phomellolitepos.database.Order_Type_Tax;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.Payment;
import org.phomellolitepos.database.Sys_Tax_Group;
import org.phomellolitepos.database.Tax_Master;
import org.phomellolitepos.database.User;
import org.phomellolitepos.printer.MemInfo;
import org.phomellolitepos.printer.PrintLayout;
import org.phomellolitepos.printer.ThreadPoolManager;
import org.phomellolitepos.utils.HandlerUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

public class VehicleIN_Fragment extends Fragment {
    private static final String TAG = "PrinterTestDemo";
    EditText edt_vehicleno,edt_advanceAmnt,edt_nfc;
    EditText edt_mobileno;
    TextView tv_chargeamnt;
    RadioButton rd_btn2w, rd_btn3w, rd_btn4w, rd_btnstaff;
    LinearLayout ll_bike,ll_auto,ll_car,ll_staff;
    View view;
    Button btn_vehiclein;
    String str,strItcode="";
    String date, modified_by;
    String objRfid="";
    String strOrderNo = "";
    Lite_POS_Device lite_pos_device;
    String part2;
    Database db;
    String str_advanceamnt;
    String PayId="1";
    SQLiteDatabase database;
    String orderId = null;
    String liccustomerid;
    Contact contact;
    String strCTCode;
    String itemPrice;
    String decimal_check;
    String strad_mnt="";
    String strNfctag_value="0";
    double damnt=0d;
    Button btn_checkout;
    String strTagId=null;
    ArrayList<Item_Group_Tax> item_group_taxArrayList;
    Sys_Tax_Group sys_tax_group;
    private static NfcAdapter nfcAdapter;
    private static PendingIntent mPendingIntent;
    String serial_no, android_id, myKey, device_id,imei_no;
    private static IntentFilter[] mFilters;
    private static String[][] mTechLists;
    private Intent mIntent = null;
    Orders objOrders;
    Dialog listDialog;
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    ArrayList<Order_Type_Tax> order_type_taxArrayList;
    public VehicleIN_Fragment() {
        //required empty public constructor.
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.contentfrag_parkingindustry, container, false);

        edt_vehicleno = (EditText) view.findViewById(R.id.edt_vehicleno);
        edt_mobileno = (EditText) view.findViewById(R.id.edt_mobilenov);
        edt_nfc=(EditText)view.findViewById(R.id.edt_nfc);
        rd_btn2w = (RadioButton) view.findViewById(R.id.radio_2w);
        rd_btn3w = (RadioButton) view.findViewById(R.id.radio_3w);
        rd_btn4w = (RadioButton) view.findViewById(R.id.radio_4w);
        rd_btnstaff = (RadioButton) view.findViewById(R.id.radio_staff);

        ll_bike=(LinearLayout) view.findViewById(R.id.ll_bike);
        ll_auto=(LinearLayout) view.findViewById(R.id.ll_auto);
        ll_car=(LinearLayout) view.findViewById(R.id.ll_car);
        ll_staff=(LinearLayout) view.findViewById(R.id.ll_staff);


        btn_vehiclein = (Button) view.findViewById(R.id.btn_vehicleinn);
        edt_advanceAmnt= (EditText) view.findViewById(R.id.edt_advanceamnt);
        tv_chargeamnt=(TextView)view.findViewById(R.id.tv_chargingamnt);
        btn_checkout=(Button)view.findViewById(R.id.btn_checkout);
        //onRadioButtonClicked(view);

        db = new Database(getActivity());
        database = db.getWritableDatabase();
        modified_by=Globals.user;
        serial_no = Build.SERIAL;
        android_id = android.provider.Settings.Secure.getString(getActivity().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        myKey = serial_no + android_id;

        final TelephonyManager mTelephony = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

           // return false;
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            device_id = android.provider.Settings.Secure.getString(
                    getActivity().getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
        } else {
            if (mTelephony.getDeviceId() != null) {
                device_id = mTelephony.getDeviceId();
            } else {
                device_id = android.provider.Settings.Secure.getString(
                        getActivity().getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }

        }
     //   device_id = telephonyManager.getDeviceId();
        //  imei_no=telephonyManager.getImei();
      /*      Random rand = new Random();

            // Generate random integers in range 0 to 999
            int rand_int1 = rand.nextInt(1000);
            int rand_int2 = rand.nextInt(1000);

            // Print random integers
            System.out.println("Random Integers: " + rand_int1);
            System.out.println("Random Integers: " + rand_int2);*/


        if(Globals.objsettings.getIs_singleWindow().equals("true")){

            btn_vehiclein.setVisibility(View.GONE);
        }
if(Globals.objsettings.getIs_NFC().equals("true")){
    edt_nfc.requestFocus();
    edt_nfc.selectAll();
  // if(strTagId!=null) {

       // Intent intent= getActivity().getIntent();
       // strTagId=rand_int1+"";
    //if(!getArguments().getParcelable("TagId").equals(null)) {
/*Intent intent=getActivity().getIntent();
    resolveIntent(intent);*/
    Bundle bundle = this.getArguments();
    if (bundle != null) {
        strTagId = getArguments().getString("TagId");
        edt_nfc.setVisibility(View.VISIBLE);
        objOrders = Orders.getOrders(getActivity(), database, "  WHERE RFID = '" + strTagId + "' and order_status = '"+"OPEN"+"' ");
        if(objOrders==null) {
            edt_nfc.setText(strTagId);
            edt_vehicleno.requestFocus();
          objRfid="";
        }
        else{
            objRfid=objOrders.getRFID();
            Toast.makeText(getActivity(),"Vehicle Already IN With this Tag",Toast.LENGTH_LONG).show();
            edt_nfc.setText("");

        }

    }
    else{

    }

   // }
    //}
   /* else if(strTagId==null){
       // strTagId=rand_int1+"";
        edt_nfc.setVisibility(View.VISIBLE);
        edt_nfc.setText(strTagId);

    }*/

}
else{
   // strTagId=rand_int1+"";
    edt_nfc.setVisibility(View.GONE);
    //edt_nfc.setText(strTagId);

}
        lite_pos_device = Lite_POS_Device.getDevice(getActivity(), "", database);
        try {
            if (lite_pos_device != null) {
                liccustomerid = lite_pos_device.getLic_customer_license_id();
            }
        } catch (Exception e) {

        }

        try {
            decimal_check = Globals.objLPD.getDecimal_Place();

        } catch (Exception ex) {
            decimal_check = "1";
        }

        if(Globals.objsettings.getIs_singleWindow().equals("true")){
            btn_checkout.setVisibility(View.VISIBLE);
        }

        if(Globals.objsettings.getIs_NFC().equals("true")) {
           // if(strTagId!=null) {
                resolveIntent(strTagId);
           // }
        }
        rd_btn2w.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                boolean checked = ((RadioButton) v).isChecked();
                // Check which radiobutton was pressed
                if (checked){
                    str = "2W";
                    strItcode="TRRIGER_POS-I-1";
                    getPrice(strItcode);
                    rd_btn3w.setChecked(false);
                    rd_btn4w.setChecked(false);
                    rd_btnstaff.setChecked(false);
                }
                else{


                }
            }
        });

        ll_bike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                str = "2W";
                strItcode="TRRIGER_POS-I-1";
                getPrice(strItcode);
                rd_btn2w.setChecked(true);
                rd_btn3w.setChecked(false);
                rd_btn4w.setChecked(false);
                rd_btnstaff.setChecked(false);
            }
        });

        rd_btn3w.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((RadioButton) v).isChecked();
                // Check which radiobutton was pressed
                if (checked){
                    str = "3W";
                    strItcode="TRRIGER_POS-I-2";
                    getPrice(strItcode);
                    rd_btn2w.setChecked(false);
                    rd_btn4w.setChecked(false);
                    rd_btnstaff.setChecked(false);
                }
                else{

                }
            }
        });
        ll_auto.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                str = "3W";
                strItcode="TRRIGER_POS-I-2";
                getPrice(strItcode);
                rd_btn3w.setChecked(true);
                rd_btn2w.setChecked(false);
                rd_btn4w.setChecked(false);
                rd_btnstaff.setChecked(false);
            }
        });
        rd_btn4w.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((RadioButton) v).isChecked();
                // Check which radiobutton was pressed
                if (checked){
                    str = "4W";
                    strItcode="TRRIGER_POS-I-3";
                    getPrice(strItcode);
                    rd_btn2w.setChecked(false);
                    rd_btn3w.setChecked(false);
                    rd_btnstaff.setChecked(false);
                }
                else{

                }
            }
        });

        ll_car.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                str = "4W";
                strItcode="TRRIGER_POS-I-3";
                getPrice(strItcode);
                rd_btn4w.setChecked(true);
                rd_btn2w.setChecked(false);
                rd_btn3w.setChecked(false);
                rd_btnstaff.setChecked(false);
            }
        });
        rd_btnstaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((RadioButton) v).isChecked();
                // Check which radiobutton was pressed
                if (checked){
                    str = "Staff";
                    strItcode="TRRIGER_POS-I-4";
                    getPrice(strItcode);
                    rd_btn2w.setChecked(false);
                    rd_btn3w.setChecked(false);
                    rd_btn4w.setChecked(false);
                }
                else{

                }
            }
        });
       ll_staff.setOnClickListener(new View.OnClickListener()
       {
           @Override
           public void onClick(View v)
           {

                   str = "Staff";
                   strItcode="TRRIGER_POS-I-4";
                   getPrice(strItcode);
                   rd_btnstaff.setChecked(true);
                   rd_btn2w.setChecked(false);
                   rd_btn3w.setChecked(false);
                   rd_btn4w.setChecked(false);

           }
       });

        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_mobile_no= edt_mobileno.getText().toString();
                String str="";
                if (rd_btn2w.isChecked()){
                    str = "2W";
                    strItcode="TRRIGER_POS-I-1";
                }
                if (rd_btn3w.isChecked()){
                    str = "3W";
                    strItcode="TRRIGER_POS-I-2";
                }
                if (rd_btn4w.isChecked()){
                    str = "4W";
                    strItcode="TRRIGER_POS-I-3";
                }
                if (rd_btnstaff.isChecked()){
                    str = "Staff";
                    strItcode="TRRIGER_POS-I-4";
                }
                if(Globals.objsettings.getIs_NFC().equals("true")){
                    if(edt_nfc.getText().toString().length()==0) {
                        edt_nfc.setError("Please Scan NFC Tag");

                        return;
                    }
                    if(edt_nfc.getText().toString().equals(objOrders.getRFID())){
                        edt_nfc.setError(" Vehicle Already IN With this Tag, Check With other");
                        edt_vehicleno.setText("");
                        edt_mobileno.setText("");
                        tv_chargeamnt.setText("");
                        rd_btnstaff.setChecked(false);
                        rd_btn4w.setChecked(false);
                        rd_btn3w.setChecked(false);
                        rd_btn2w.setChecked(false);
                        Toast.makeText(getActivity(), " Vehicle Already IN With this Tag", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if(Globals.objsettings.getIs_ValidateVehNo().equals("true")){
                    if(edt_vehicleno.getText().toString().length()==0) {
                        edt_vehicleno.setError("Please Enter Vehicle No");
                        return;
                    }
                }
                if(Globals.objsettings.getIs_ValidateMobNo().equals("true")){
                    if(edt_mobileno.getText().toString().length()==0) {
                        edt_mobileno.setError("Please Enter Mobile No");
                        return;
                    }
                }
               if(str.isEmpty())
                {
                    Toast.makeText(getActivity(),"Please Sselect Vehicle Type",Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    addContact(str_mobile_no);
                    // Saving Vehicle Details to Orders and Order Detail Table
                    SaveVehicleIN("checkout");
                }
            }
        });


        btn_vehiclein.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String str="",strItcode;
                String str_vehicleno=edt_vehicleno.getText().toString().trim();
                String str_mobile_no= edt_mobileno.getText().toString().trim();
                strad_mnt= edt_advanceAmnt.getText().toString().trim();


                if(!strad_mnt.equals(""))
                {
                    if(strad_mnt.equals("."))
                    {
                        edt_advanceAmnt.setError("Invalid Amount");
                        edt_advanceAmnt.requestFocus();
                        return;
                    }
                    if(Double.parseDouble(strad_mnt)>2000)
                    {
                        edt_advanceAmnt.setError("Can't accept Advance Amount Greater then 2000 ");
                        edt_advanceAmnt.requestFocus();
                        return;
                    }
                    else{
                         damnt = Double.parseDouble(strad_mnt);
                         str_advanceamnt = Globals.myNumberFormat2Price(damnt, decimal_check);
                        }
                }
                else{
                      str_advanceamnt=Globals.myNumberFormat2Price(damnt, decimal_check);
                    }
                if (rd_btn2w.isChecked()){
                    str = "2W";
                    strItcode="TRRIGER_POS-I-1";
                }
                if (rd_btn3w.isChecked()){
                    str = "3W";
                    strItcode="TRRIGER_POS-I-2";
                }
                if (rd_btn4w.isChecked()){
                    str = "4W";
                    strItcode="TRRIGER_POS-I-3";
                }
                if (rd_btnstaff.isChecked()){
                    str = "Staff";
                    strItcode="TRRIGER_POS-I-4";
                }
                if(Globals.objsettings.getIs_NFC().equals("true")){
                    if(edt_nfc.getText().toString().length()==0) {
                        edt_nfc.setError("Please Scan NFC Tag");

                        return;
                    }

                     if(edt_nfc.getText().toString().equals(objRfid)){
                        edt_nfc.setError(" Vehicle Already IN With this Tag, Check With other");
                        edt_vehicleno.setText("");
                        edt_mobileno.setText("");
                        tv_chargeamnt.setText("");
                        rd_btnstaff.setChecked(false);
                        rd_btn4w.setChecked(false);
                        rd_btn3w.setChecked(false);
                        rd_btn2w.setChecked(false);
                        Toast.makeText(getActivity(), " Vehicle Already IN With this Tag", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

 if(Globals.objsettings.getIs_ValidateVehNo().equals("true")){
    if(edt_vehicleno.getText().toString().length()==0) {
        edt_vehicleno.setError("Please Enter Vehicle No");
        edt_vehicleno.requestFocus();
        return;
    }
}
 if(Globals.objsettings.getIs_ValidateMobNo().equals("true")){
    if(edt_mobileno.getText().toString().length()==0) {
        edt_mobileno.setError("Please Enter Mobile No");
        edt_mobileno.requestFocus();
        return;
    }
}
                if(str.isEmpty())
                {
                    Toast.makeText(getActivity(),"Please Select  Vehicle Type",Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    // Add Contact Function

                    if(!edt_vehicleno.getText().toString().trim().equals(""))
                    {
                        SQLiteDatabase database=db.getReadableDatabase();
                        String q="SELECT  od.order_code,od_det.discount ,od_det.sale_price ,od.order_status,od.table_code,od.remarks,od.modified_date,ct.contact_1,od.RFID FROM orders od LEFT JOIN  contact ct ON ct.contact_code=od.contact_code left join order_detail od_det ON od_det.order_code=od.order_code  WHERE od.is_active='1' and  od.order_status='OPEN' and od.table_code='"+str_vehicleno+"' and od.remarks='"+str+"'";
                        Cursor cursor=database.rawQuery(q,null);
                        if(cursor.getCount()>0)
                        {
                            Toast.makeText(getActivity(),"Vehicle Already IN",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

               addContact(str_mobile_no);
               // Saving Vehicle Details to Orders and Order Detail Table
               SaveVehicleIN("VehicleIn");

                }
              // Toast.makeText(getActivity(),str,Toast.LENGTH_LONG).show();
            }
        });


        return view;
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


    private void startWhatsApp(String ordercode,String strFlag) {
        String strContct = "";
        contact = Contact.getContact(getActivity(), database, db, " where is_active ='1' and contact_1='" +edt_mobileno.getText().toString()+ "'");
        if (contact == null) {
        } else {

            if(Globals.objLPR.getCountry_Id().equals("99")) {

                strContct = "91"+contact.get_contact_1();

            }
            if(Globals.objLPR.getCountry_Id().equals("114")) {

                strContct = "965"+contact.get_contact_1();

            }
            if(Globals.objLPR.getCountry_Id().equals("221")) {
                strContct = "971"+contact.get_contact_1();
            }
        }
        final File file = new File(Globals.folder + Globals.pdffolder
                + "/" +ordercode +"80mm" + ".pdf");
        // Toast.makeText(getApplicationContext(),file+"",Toast.LENGTH_SHORT).show();
        if (contactExists(getActivity(), strContct)) {
            boolean installed = appInstalledOrNot("com.whatsapp");
            if (installed) {
                //This intent will help you to launch if the package is already installed
                try {
                    openWhatsApp(file,getActivity(),strContct,strFlag);


                } catch (Exception e) {
                    Globals.AppLogWrite("Contact Exception  "+e.getMessage());
                    /// Toast.makeText(getApplicationContext(),"Exception"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), "Please Install whatsapp first!", Toast.LENGTH_SHORT).show();
                //call_remaining_code();
            }

        }
        else {

            if (SaveContact()) {
                Toast.makeText(getActivity(), "Contact Saved in Ur PhoneContacts!", Toast.LENGTH_SHORT).show();
                //  finish();
              // call_remaining_code();
                boolean installed = appInstalledOrNot("com.whatsapp");
                if (installed) {
                    //This intent will help you to launch if the package is already installed
                    try {
                        // String id = "Message +91 9024490780";
                        openWhatsApp(file,getActivity(),strContct,strFlag);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(getActivity(), "Error saving contact", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openWhatsApp(File file,Context context,String contactnumbr,String strFlag) {
        Uri path = FileProvider.getUriForFile(context, "com.org.phomellolitepos.myfileprovider", file);
        String toNumber = contactnumbr;
        // contains spaces.
        toNumber = toNumber.replace("+", "").replace(" ", "");
        Intent pdfOpenintent = new Intent(Intent.ACTION_SEND);
        pdfOpenintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pdfOpenintent.setType("application/pdf");
        pdfOpenintent.setPackage("com.whatsapp");
        pdfOpenintent.putExtra("jid", toNumber + "@s.whatsapp.net");
        pdfOpenintent.putExtra(Intent.EXTRA_STREAM, path);
        // context.startActivity(sendIntent);
        try {
            startActivityForResult(pdfOpenintent, 1);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }

    }

    public boolean contactExists(Context context, String number) {
/// number is the phone number
        if(!number.isEmpty()) {
            Uri lookupUri = Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(number));
            String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
            Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
            try {
                if (cur != null && cur.moveToFirst()) {
                    return true;
                }
            } finally {
                if (cur != null)
                    cur.close();
            }
        }
        return false;
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        boolean app_installed = false;
        try {
            //  Toast.makeText(getApplicationContext(),app_installed+"App Installed",Toast.LENGTH_SHORT).show();
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    boolean SaveContact() {
        //Get text
        String szFirstname = contact.get_name(),
                szPhone = "+91" + " " + contact.get_contact_1();

        Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(), R.drawable.user);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        //Create a new contact entry!

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex = ops.size();

        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //INSERT NAME
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, szFirstname) // Name of the person
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, szFirstname) // Name of the person
                .build());

        //INSERT PHONE
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, szPhone) // Number of the person
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                .build());

        //INSERT PITURE
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO,
                        stream.toByteArray())
                .build());

        Uri newContactUri = null;

        try {
            ContentProviderResult[] res = getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            if (res != null && res[0] != null) {
                newContactUri = res[0].uri;
            }
        } catch (RemoteException e) {
            // error
            newContactUri = null;
        } catch (OperationApplicationException e) {
            // error
            newContactUri = null;
        }
        return newContactUri != null;
    }

    public void SaveVehicleIN(String strFlag){
        Order_Detail objOrderDetail;
        Orders objOrder;
        Order_Payment objOrderPayment = null;
        if(Globals.objsettings.getIs_NFC().equals("true")){
            strNfctag_value=edt_nfc.getText().toString().trim();
        }
        else{
            strNfctag_value="0";
        }
        strad_mnt= edt_advanceAmnt.getText().toString().trim();

        if(!strad_mnt.equals(""))
        {
            if(strad_mnt.equals("."))
            {
                edt_advanceAmnt.setError("Invalid Amount");
                edt_advanceAmnt.requestFocus();
                return;
            }
            else
                {
                    damnt = Double.parseDouble(strad_mnt);
                    str_advanceamnt = Globals.myNumberFormat2Price(damnt, decimal_check);
                }

        }
        else{
            str_advanceamnt=Globals.myNumberFormat2Price(damnt, decimal_check);
            }

        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        date = format.format(d);

        Orders objOrder1 = Orders.getOrders(getActivity(), database, "  order By order_id Desc LIMIT 1");
        Last_Code last_code = Last_Code.getLast_Code(getActivity(), "", database);

        if (last_code == null) {
            if (objOrder1 == null) {
                strOrderNo = lite_pos_device.getDevice_Symbol() + "-" + 1;
            } else {
                strOrderNo = lite_pos_device.getDevice_Symbol() + "-" + (Integer.parseInt(objOrder1.get_order_id()) + 1);
            }
        } else {
            if (last_code.getlast_order_code().equals("0")) {

                if (objOrder1 == null) {
                    strOrderNo = lite_pos_device.getDevice_Symbol() + "-" + 1;
                } else {
                    strOrderNo = lite_pos_device.getDevice_Symbol() + "-" + (Integer.parseInt(objOrder1.get_order_id()) + 1);
                }
            } else {
                if (objOrder1 == null) {
                    String code = last_code.getlast_order_code();
                    String[] strCode = code.split("-");
                    part2 = strCode[1];
                    orderId = (Integer.parseInt(part2) + 1) + "";
                    strOrderNo = lite_pos_device.getDevice_Symbol() + "-" + (Integer.parseInt(part2) + 1);
                } else {
                    strOrderNo = lite_pos_device.getDevice_Symbol() + "-" + (Integer.parseInt(objOrder1.get_order_id()) + 1);
                }
            }
        }

        String locCode = null;
        try {
            locCode = Globals.objLPD.getLocation_Code();
        } catch (Exception ex) {
            locCode = "1";
        }

        objOrder = new Orders(getActivity(), orderId, liccustomerid, locCode, "", strOrderNo, date, strCTCode,
                strNfctag_value, Globals.TotalItem + "", Globals.TotalQty + "",
                Globals.TotalItemPrice + "", "" + "", "0", itemPrice, "0" + "",
                "0" + "", "z-1", "0", "1", "1", modified_by, date, "N", "OPEN", str, edt_vehicleno.getText().toString().toUpperCase(), date,strNfctag_value);

        long l = objOrder.insertOrders(database);
        if (l > 0) {

            Item item = Item.getItem(getActivity(), "Where item_code='" + strItcode + "'", database, db);
            if (item != null) {
                Item_Location item_location = Item_Location.getItem_Location(getActivity(), "WHERE item_code ='" + strItcode + "'", database);

                objOrderDetail = new Order_Detail(getActivity(), null, liccustomerid, strOrderNo,
                        "", item.get_item_code(), "1", item_location.get_cost_price(), item_location.get_selling_price(), item.get_is_inclusive_tax(),
                        "1", "0", str_advanceamnt, item_location.get_new_sell_price(), "0", "false",item.get_unit_id(),"");
                long od = objOrderDetail.insertOrder_Detail(database);
                if (od > 0) {
                    ArrayList<Order_Item_Tax> order_item_tax = Globals.order_item_tax;
                    if(Globals.order_item_tax.size()>0){
                    Order_Detail_Tax objOrderDetailTax;
                    for (int cnt = 0; cnt < order_item_tax.size(); cnt++) {
                        Order_Item_Tax OrdItemTax = order_item_tax.get(cnt);

                        objOrderDetailTax = new Order_Detail_Tax(getActivity(), null, strOrderNo, OrdItemTax.get_sr_no(), OrdItemTax.get_item_code(), OrdItemTax.get_tax_id()
                                , OrdItemTax.get_tax_type(), OrdItemTax.get_rate(), OrdItemTax.get_value());

                        long o = objOrderDetailTax.insertOrder_Detail_Tax(database);
                        if (o > 0) {
                        }

                    }
                    }
/*
                    if (PayId.equals("2")) {
                        if (sett.getPrinterId().equals("5")) {
                            objOrderPayment = new Order_Payment(getApplicationContext(), null, Globals.Device_Code, strOrderNo, "1", edt_net_amount.getText().toString(),
                                    PayId, "", "", Globals.Param1, Globals.Param2, strBankCode, "");
                        } else {
                            objOrderPayment = new Order_Payment(getApplicationContext(), null, Globals.Device_Code, strOrderNo, "1", edt_net_amount.getText().toString(),
                                    PayId, "", "", Globals.Param1, Globals.Param2, strBankCode, "");
                        }
                    } else {*/

                            //  ppt8555_parking(objOrder);



  /*                  Contact contact = Contact.getContact(getActivity(), database, db, " WHERE contact_1='" + mobileno + "'");
                    if (contact == null) {
                    } else {
                        if (contact.get_email_1().equals("")) {
                        } else {
                            if (Globals.objsettings.get_Is_email().equals("true")) {
                                String strEmail = contact.get_email_1();
                                send_email(strEmail);
                                Globals.AppLogWrite("sender email" + strEmail);
                            }
                        }
                    }*/
                    if (Globals.objLPR.getproject_id().equals("cloud") && Globals.objsettings.get_IsOnline().equals("true")) {
                        Sendorder_BackgroundAsyncTask order = new Sendorder_BackgroundAsyncTask();
                        order.execute();
                    }

                     try {

                pdfPerform_80mm(strFlag);

            } catch (Exception e) {

            }
                    if(Globals.objsettings.get_Is_File_Share().equals("true")) {
                        showdialog(strFlag);
                    }
                    else{
                        call_remainingcode(strFlag);
                    }
                 /*   if (Globals.objsettings.get_Is_File_Share().equals("true")) {
                      runthread2(strFlag);
                     runthread1(strFlag);

                    } else {
                        runthread2(strFlag);
                    }*/


                            // Toast.makeText(getActivity(), "Vehicle In Record Save Successfully", Toast.LENGTH_SHORT).show();

                            //  getFragmentManager().beginTransaction().detach(this).attach(this).commit();

                    }
                }
            }

    }

    private void runthread2(String strFlag){
        Thread t=new Thread(){
            @Override
            public void run() {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        call_remainingcode(strFlag);

                    }
                });
            }
        };t.start();
        t.setPriority(Thread.MAX_PRIORITY);


    }
    private void runthread1(String strFlag){

                final Dialog listDialog2 = new Dialog(getActivity());
                listDialog2.setTitle("File Share");
                LayoutInflater li1 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v1 = li1.inflate(R.layout.add_remarks_dialog, null, false);
                listDialog2.setContentView(v1);
                listDialog2.setCancelable(false);
                final EditText edt_remark = (EditText) listDialog2.findViewById(R.id.edt_remark);
                edt_remark.setVisibility(View.GONE);
                Button btnButton = (Button) listDialog2.findViewById(R.id.btn_save);
                btnButton.setText("Share");
                Button btnClear = (Button) listDialog2.findViewById(R.id.btn_clear);
                listDialog2.show();
                btnClear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        call_remainingcode(strFlag);

                    }
                });
                btnButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (edt_mobileno.getText().toString().equals("")|| edt_mobileno.length()==0) {
                            call_remainingcode(strFlag);
                            // Toast.makeText(getActivity(),"call rem code",Toast.LENGTH_SHORT).show();
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {

                                    //  Toast.makeText(getApplicationContext(),"startWhatsapp",Toast.LENGTH_SHORT).show();
                                    startWhatsApp(strOrderNo,strFlag);


                                }
                            });
                        }
                        listDialog2.dismiss();
                    }
                });




    }
    public void call_remainingcode(String strFlag){

        if (strFlag.equals("checkout")) {
            CheckOutVehicle(strOrderNo);
    /*        try {

                pdfPerform_80mm(strFlag);

            } catch (Exception e) {

            }*/
        } else if (strFlag.equals("VehicleIn")) {

            if(Globals.objsettings.getPrinterId().equals("0")){
                Toast.makeText(getActivity(), "Vehicle In Record Save Successfully!", Toast.LENGTH_SHORT).show();
                Intent i= new Intent(getActivity(),ParkingIndustryActivity.class);
                startActivity(i);

            }
            else {
               // Toast.makeText(getActivity(), "Vehicle In Record Save Successfully!", Toast.LENGTH_SHORT).show();

                Intent launchIntent = new Intent(getActivity(), PrintLayout.class);
                launchIntent.putExtra("strflag", "VehicleIn");
                launchIntent.putExtra("strOrderNo", strOrderNo);
                getActivity().startActivity(launchIntent);

            }
            /*try {

                pdfPerform_80mm(strFlag);

            } catch (Exception e) {

            }*/
            edt_vehicleno.setText("");
            edt_mobileno.setText("");
            edt_advanceAmnt.setText("");
            tv_chargeamnt.setText("");
            rd_btn2w.setChecked(false);
            rd_btn3w.setChecked(false);
            rd_btn4w.setChecked(false);
            rd_btnstaff.setChecked(false);
        }
    }
    private void send_email(String strEmail) {
        try {
            String[] recipients = strEmail.split(",");
            final SendEmailAsyncTask email = new SendEmailAsyncTask();


            Contact contact = Contact.getContact(getActivity(), database, db, " WHERE contact_code = '" + Globals.strContact_Code + "'");
            Globals.AppLogWrite("settings email"+ Globals.objsettings.get_Email());
            Globals.AppLogWrite("settings Password"+ Globals.objsettings.get_Password());
            Globals.AppLogWrite("settings host"+ Globals.objsettings.get_Host());
            Globals.AppLogWrite("settings port"+ Globals.objsettings.get_Port());

            email.m = new GMailSender(Globals.objsettings.get_Email(), Globals.objsettings.get_Password(), Globals.objsettings.get_Host(), Globals.objsettings.get_Port());
            email.m.set_from(Globals.objsettings.get_Email());
            email.m.setBody("Dear Customer " + contact.get_name() + ", PFA Customer Copy of your Order no : " + strOrderNo + " ");
            Globals.AppLogWrite("recipients"+ recipients);

            email.m.set_to(recipients);
            email.m.set_subject("Confirmation of your Order " + strOrderNo + " Mail");
//            email.m.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/" + strOrderNo + ".pdf");
            email.m.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/" + "TriggerPOS" + "/" + "PDF Report" + "/" + strOrderNo +"80mm"+".pdf");
            email.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
        GMailSender m;


        public SendEmailAsyncTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (m.send()) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            //Toast.makeText(getApplicationContext(), "Email sent.", Toast.LENGTH_SHORT).show();
                        }
                    });

//                    activity.displayMessage("Email sent.");

                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            //Toast.makeText(getApplicationContext(), "Email failed to send.", Toast.LENGTH_SHORT).show();
                        }
                    });

//                    activity.displayMessage("Email failed to send.");
                }

                return true;
            } catch (AuthenticationFailedException e) {
                Log.e(SendEmailAsyncTask.class.getName(), "Bad account details");
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        //Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Authentication failed.");
                return false;
            } catch (MessagingException e) {
                Log.e(SendEmailAsyncTask.class.getName(), "Email failed");
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        //Toast.makeText(getApplicationContext(), "Email failed to send.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Email failed to send.");
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        //Toast.makeText(getApplicationContext(), "Unexpected error occured.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Unexpected error occured.");
                return false;
            }
        }

    }

    class Sendorder_BackgroundAsyncTask extends AsyncTask<Void, Void, Boolean> {

        PaymentActivity activity;

        public Sendorder_BackgroundAsyncTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                Orders order=new Orders(getActivity());

                order.sendOn_Server(getActivity(), database, db, "Select * From orders WHERE is_push = 'N'",liccustomerid,serial_no,android_id,myKey);

                           /* if(result_order.equals("1")){
                                Toast.makeText(activity, "Data Post Successfully", Toast.LENGTH_SHORT).show();
                            }*/
                //Toast.makeText(getApplicationContext(), "Email sent.", Toast.LENGTH_SHORT).show();


//                    activity.displayMessage("Email sent.");


                return true;


            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        //Toast.makeText(getApplicationContext(), "Unexpected error occured.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Unexpected error occured.");
                return false;
            }
        }

    }

    public void addContact(String str_mobile_no){

        contact = Contact.getContact(getActivity(), database, db, "WHERE contact_1='" + str_mobile_no + "'");

        if (contact == null) {
            Contact objCT1 = Contact.getContact(getActivity(), database, db, " Where contact_code like  '" + Globals.objLPD.getDevice_Symbol() + "-CT-%'  order By contact_id Desc LIMIT 1");

            if (objCT1 == null) {
                strCTCode = Globals.objLPD.getDevice_Symbol() + "-" + "CT-" + 1;
            } else {
                strCTCode = Globals.objLPD.getDevice_Symbol() + "-" + "CT-" + (Integer.parseInt(objCT1.get_contact_code().toString().replace(Globals.objLPD.getDevice_Symbol() + "-CT-", "")) + 1);
            }

            contact = new Contact(getActivity(), null, Globals.Device_Code, strCTCode, "",
                    edt_vehicleno.getText().toString(), "", "", "", "", edt_mobileno.getText().toString(), "", "", "", "1", modified_by, "N", "", date, "0", "", "0", "0","1");
            //database.beginTransaction();
            long l1 = contact.insertContact(database);
            if (l1 > 0) {
                Address address_class = new Address(getActivity(), null, Globals.Device_Code, strCTCode, "AC-1",
                        "0", "", "0", "0", "0", "0", "0", "1", modified_by, date, "N");
                long a = address_class.insertAddress(database);
                if (a > 0) {
                    Address_Lookup address_lookup = new Address_Lookup(getActivity(), null, Globals.Device_Code, strCTCode, "1",
                            strCTCode, "N");
                    long b = address_lookup.insertAddress_Lookup(database);

                }

                Contact_Bussiness_Group contact_bussiness_group = new Contact_Bussiness_Group(getActivity(), strCTCode, "BGC-1");
                long ab = contact_bussiness_group.insertContact_Bussiness_Group(database);
                if (ab > 0) {

                }
            }
        } else {
            strCTCode = contact.get_contact_code();
        }

    }
public void getPrice(String itemcode){
    if(!strItcode.equals("")) {
        contact = Contact.getContact(getActivity(), database, db, "WHERE contact_1='" + edt_mobileno.getText().toString() + "'");

        Item item = Item.getItem(getActivity(), "Where item_code='" + itemcode + "'", database, db);
        if (item != null) {
            Item_Location item_location = Item_Location.getItem_Location(getActivity(), "WHERE item_code ='" + itemcode + "'", database);
            item_group_taxArrayList = Item_Group_Tax.getAllItem_Group_Tax(getActivity(), "Where item_group_code = '" + item.get_item_code() + "'", database, db);
            Double iTax = 0d;
            Double iTaxTotal = 0d;
            Double sprice=0d;

                 if (item_group_taxArrayList.size() > 0) {
                     if(Globals.objLPR.getCountry_Id().equals("99")) {
                         if (contact != null) {
                             if (contact.getIs_taxable().equals("1")) {
                                 if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                     for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                         sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getActivity(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                         if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {
                                             iTax = 0d;
                                             Tax_Master tax_master = Tax_Master.getTax_Master(getActivity(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                             Double iPrice = Double.parseDouble(item_location.get_selling_price());
                                             if (tax_master.get_tax_type().equals("P")) {
                                                 iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                             } else {
                                                 iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                             }

                                             Order_Item_Tax order_item_tax = new Order_Item_Tax(getActivity(), "", "", Globals.SRNO + "", item.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                             Globals.order_item_tax.add(order_item_tax);
                                             iTaxTotal += iTax;
                                         }
                                     }

                                     sprice = Double.parseDouble(item_location.get_selling_price()) + iTaxTotal;
                                 } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                     for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                         sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getActivity(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                         if (sys_tax_group.get_tax_master_id().equals("3")) {
                                             iTax = 0d;
                                             Tax_Master tax_master = Tax_Master.getTax_Master(getActivity(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                             Double iPrice = Double.parseDouble(item_location.get_selling_price());
                                             if (tax_master.get_tax_type().equals("P")) {
                                                 iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                             } else {
                                                 iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                             }

                                             Order_Item_Tax order_item_tax = new Order_Item_Tax(getActivity(), "", "", Globals.SRNO + "", item.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                             Globals.order_item_tax.add(order_item_tax);
                                             iTaxTotal += iTax;
                                         }
                                     }
                                     sprice = Double.parseDouble(item_location.get_selling_price()) + iTaxTotal;
                                 }


                             } else {
                                 sprice = Double.parseDouble(item_location.get_selling_price());
                             }
                         } else if (contact == null) {
                             for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                                 sys_tax_group = Sys_Tax_Group.getSys_Tax_Group(getActivity(), "WHERE tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'");

                                 if (sys_tax_group.get_tax_master_id().equals("1") || sys_tax_group.get_tax_master_id().equals("2")) {

                                     iTax = 0d;
                                     Tax_Master tax_master = Tax_Master.getTax_Master(getActivity(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                     Double iPrice = Double.parseDouble(item_location.get_selling_price());
                                     if (tax_master.get_tax_type().equals("P")) {
                                         iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                     } else {
                                         iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                     }

                                     Order_Item_Tax order_item_tax = new Order_Item_Tax(getActivity(), "", "", Globals.SRNO + "", item.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                     Globals.order_item_tax.add(order_item_tax);
                                     iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));
                                 }
                             }
                             sprice = Double.parseDouble(item_location.get_selling_price()) + iTaxTotal;
                         }
                         tv_chargeamnt.setText("Fare Amount : " + Globals.myNumberFormat2Price(sprice, decimal_check));
                         itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(item_location.get_selling_price()), decimal_check);

                         if (Globals.objsettings.getIs_FillAdvanceAmnt().equals("true")) {
                             edt_advanceAmnt.setText(itemPrice);
                         }
                     }

                    else if(Globals.objLPR.getCountry_Id().equals("114")) {
                         if (contact != null) {
                             if (contact.getIs_taxable().equals("1")) {
                                 if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                     for (int i = 0; i < item_group_taxArrayList.size(); i++) {



                                             iTax = 0d;
                                             Tax_Master tax_master = Tax_Master.getTax_Master(getActivity(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                             Double iPrice = Double.parseDouble(item_location.get_selling_price());
                                             if (tax_master.get_tax_type().equals("P")) {
                                                 iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                             } else {
                                                 iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                             }

                                             Order_Item_Tax order_item_tax = new Order_Item_Tax(getActivity(), "", "", Globals.SRNO + "", item.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                             Globals.order_item_tax.add(order_item_tax);
                                             iTaxTotal += iTax;

                                     }

                                     sprice = Double.parseDouble(item_location.get_selling_price()) + iTaxTotal;
                                 } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                     for (int i = 0; i < item_group_taxArrayList.size(); i++) {



                                             iTax = 0d;
                                             Tax_Master tax_master = Tax_Master.getTax_Master(getActivity(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                             Double iPrice = Double.parseDouble(item_location.get_selling_price());
                                             if (tax_master.get_tax_type().equals("P")) {
                                                 iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                             } else {
                                                 iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                             }

                                             Order_Item_Tax order_item_tax = new Order_Item_Tax(getActivity(), "", "", Globals.SRNO + "", item.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                             Globals.order_item_tax.add(order_item_tax);
                                             iTaxTotal += iTax;

                                     }
                                     sprice = Double.parseDouble(item_location.get_selling_price()) + iTaxTotal;
                                 }


                             } else {
                                 sprice = Double.parseDouble(item_location.get_selling_price());
                             }
                         } else if (contact == null) {
                             for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                     iTax = 0d;
                                     Tax_Master tax_master = Tax_Master.getTax_Master(getActivity(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                     Double iPrice = Double.parseDouble(item_location.get_selling_price());
                                     if (tax_master.get_tax_type().equals("P")) {
                                         iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                     } else {
                                         iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                     }

                                     Order_Item_Tax order_item_tax = new Order_Item_Tax(getActivity(), "", "", Globals.SRNO + "", item.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                     Globals.order_item_tax.add(order_item_tax);
                                     iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));

                             }
                             sprice = Double.parseDouble(item_location.get_selling_price()) + iTaxTotal;
                         }
                         tv_chargeamnt.setText("Fare Amount : " + Globals.myNumberFormat2Price(sprice, decimal_check));
                         itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(item_location.get_selling_price()), decimal_check);

                         if (Globals.objsettings.getIs_FillAdvanceAmnt().equals("true")) {
                             edt_advanceAmnt.setText(itemPrice);
                         }
                     }

                     else if(Globals.objLPR.getCountry_Id().equals("221")) {
                         if (contact != null) {
                             if (contact.getIs_taxable().equals("1")) {
                                 if (contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                     for (int i = 0; i < item_group_taxArrayList.size(); i++) {



                                         iTax = 0d;
                                         Tax_Master tax_master = Tax_Master.getTax_Master(getActivity(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                         Double iPrice = Double.parseDouble(item_location.get_selling_price());
                                         if (tax_master.get_tax_type().equals("P")) {
                                             iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                         } else {
                                             iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                         }

                                         Order_Item_Tax order_item_tax = new Order_Item_Tax(getActivity(), "", "", Globals.SRNO + "", item.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                         Globals.order_item_tax.add(order_item_tax);
                                         iTaxTotal += iTax;

                                     }

                                     sprice = Double.parseDouble(item_location.get_selling_price()) + iTaxTotal;
                                 } else if (!contact.get_zone_id().equals(Globals.objLPR.getZone_Id())) {
                                     for (int i = 0; i < item_group_taxArrayList.size(); i++) {



                                         iTax = 0d;
                                         Tax_Master tax_master = Tax_Master.getTax_Master(getActivity(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                         Double iPrice = Double.parseDouble(item_location.get_selling_price());
                                         if (tax_master.get_tax_type().equals("P")) {
                                             iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                         } else {
                                             iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                         }

                                         Order_Item_Tax order_item_tax = new Order_Item_Tax(getActivity(), "", "", Globals.SRNO + "", item.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                         Globals.order_item_tax.add(order_item_tax);
                                         iTaxTotal += iTax;

                                     }
                                     sprice = Double.parseDouble(item_location.get_selling_price()) + iTaxTotal;
                                 }


                             } else {
                                 sprice = Double.parseDouble(item_location.get_selling_price());
                             }
                         } else if (contact == null) {
                             for (int i = 0; i < item_group_taxArrayList.size(); i++) {


                                 iTax = 0d;
                                 Tax_Master tax_master = Tax_Master.getTax_Master(getActivity(), "Where tax_id = '" + item_group_taxArrayList.get(i).get_tax_id() + "'", database, db);
                                 Double iPrice = Double.parseDouble(item_location.get_selling_price());
                                 if (tax_master.get_tax_type().equals("P")) {
                                     iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                                 } else {
                                     iTax = iTax + Double.parseDouble(tax_master.get_rate());
                                 }

                                 Order_Item_Tax order_item_tax = new Order_Item_Tax(getActivity(), "", "", Globals.SRNO + "", item.get_item_code(), item_group_taxArrayList.get(i).get_tax_id(), tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, decimal_check) + "");
                                 Globals.order_item_tax.add(order_item_tax);
                                 iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, decimal_check));

                             }
                             sprice = Double.parseDouble(item_location.get_selling_price()) + iTaxTotal;
                         }
                         tv_chargeamnt.setText("Fare Amount : " + Globals.myNumberFormat2Price(sprice, decimal_check));
                         itemPrice = Globals.myNumberFormat2Price(Double.parseDouble(item_location.get_selling_price()), decimal_check);

                         if (Globals.objsettings.getIs_FillAdvanceAmnt().equals("true")) {
                             edt_advanceAmnt.setText(itemPrice);
                         }
                     }
                 }
                 else {
                     sprice = Double.parseDouble(item_location.get_selling_price());
                     tv_chargeamnt.setText("Fare Amount : "+  Globals.myNumberFormat2Price(sprice,decimal_check));
                     itemPrice=Globals.myNumberFormat2Price(Double.parseDouble(item_location.get_selling_price()),decimal_check);

                     if(Globals.objsettings.getIs_FillAdvanceAmnt().equals("true"))
                     {
                         edt_advanceAmnt.setText(itemPrice);
                     }
                 }



        }

    }

}


public void CheckOutVehicle(String orderCode){
    Orders orders = Orders.getOrders(getActivity(), database, " WHERE order_code = '" + orderCode + "'");
    orders.set_order_status("CLOSE");
    orders.set_delivery_date(date);
    long l = orders.updateOrders("order_code=?", new String[]{orderCode}, database);

    Order_Payment objOrderPayment = null;
    objOrderPayment = new Order_Payment(getActivity(), null, liccustomerid, orderCode, "1", edt_advanceAmnt.getText().toString().trim(),
            PayId, "", "", "", "", "", "");
    // }
    long op = objOrderPayment.insertOrder_Payment(database);
    if (op > 0) {
        // strFlag = "1";
        Toast.makeText(getActivity(), "Vehicle OUT Successfully!", Toast.LENGTH_SHORT).show();
        Intent launchIntent = new Intent(getActivity(), PrintLayout.class);
        launchIntent.putExtra("strflag", "Checkout");
        launchIntent.putExtra("strOrderNo", orderCode);
        getActivity().startActivity(launchIntent);
        edt_vehicleno.setText("");
        edt_mobileno.setText("");
        edt_advanceAmnt.setText("");
        tv_chargeamnt.setText("");
        rd_btn2w.setChecked(false);
        rd_btn3w.setChecked(false);
        rd_btn4w.setChecked(false);
        rd_btnstaff.setChecked(false);
    }

}
    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    public void pdfPerform_80mm(String strflagbtn){


        final Orders orders = Orders.getOrders(getActivity(), database, "WHERE order_code = '" + strOrderNo + "'");

        final Order_Detail order_detail1 = Order_Detail.getOrder_Detail(getActivity(), "WHERE order_code = '" + strOrderNo + "'",database);
        Order_Payment order_payment = null;
       if(strflagbtn.equals("checkout")) {
            order_payment = Order_Payment.getOrder_Payment(getActivity(), "WHERE order_code = '" + strOrderNo + "'", database);
       }
        int count = 0;


        File f = null;
        try {
            File sd = new File(Globals.folder + Globals.pdffolder);
            if (!sd.exists()) {
                sd.mkdirs();
            }
            //String dtt = Globals.Reportnamedate();
            f = new File(Globals.folder + Globals.pdffolder
                    + "/" + strOrderNo+ "80mm" + ".pdf");
            //if (Globals.objsettings.get_Print_Lang().equals("0")) {
            OutputStream file = new FileOutputStream(f);
            Document document = new Document(PageSize.B7);
            document.setMargins(-5f, -5f, 10f, 10f);

            PdfWriter writer = PdfWriter.getInstance(document, file);

            Image image = null;
//            image = createimage();
            if(Globals.objsettings.get_Print_Lang().equals("0")) {
                Font B12 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
                Font N10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
                Font B10 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL, BaseColor.BLACK);
                Font N9 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL, BaseColor.BLACK);
                Font N12 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);


                PdfPTable tableh = new PdfPTable(1);
                PdfPCell cellh = new PdfPCell(new Paragraph(getString(R.string.Order), B12));

                cellh.setBorder(Rectangle.NO_BORDER);
                cellh.setColspan(1);
                cellh.setHorizontalAlignment(Element.ALIGN_CENTER);

                cellh.setBackgroundColor(new BaseColor(204, 204, 204));
                tableh.addCell(cellh);

                PdfPTable table_company_name = new PdfPTable(1);

                Phrase prcommpanyname ;
                if (Globals.objLPR.getShort_companyname().isEmpty()) {
                    prcommpanyname = new Phrase("" + Globals.objLPR.getCompany_Name(), B10);
                } else {
                    prcommpanyname = new Phrase("" + Globals.objLPR.getShort_companyname(), B10);

                }
                PdfPCell cell_company_name;

                cell_company_name = new PdfPCell(prcommpanyname);
                cell_company_name.setBorder(Rectangle.NO_BORDER);

                cell_company_name.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_company_name.addCell(cell_company_name);

                table_company_name.setSpacingBefore(5.0f);

                PdfPTable table_company_adres = new PdfPTable(1);

                Phrase prcommpanyadd ;
                if (Globals.objLPR.getShort_companyname().isEmpty()) {
                    prcommpanyadd = new Phrase("" + Globals.objLPR.getAddress(), B10);
                } else {
                    prcommpanyadd = new Phrase("" + Globals.objLPR.getAddress(), B10);

                }
                PdfPCell cell_company_add;

                cell_company_add = new PdfPCell(prcommpanyadd);
                cell_company_add.setBorder(Rectangle.NO_BORDER);
                cell_company_add.setHorizontalAlignment(Element.ALIGN_CENTER);
                table_company_adres.addCell(cell_company_add);

                PdfPTable table_company_mobile = new PdfPTable(1);

                Phrase prcommpanymobile ;
                if (Globals.objLPR.getShort_companyname().isEmpty()) {
                    prcommpanymobile = new Phrase("" + Globals.objLPR.getMobile_No(), B10);
                } else {
                    prcommpanymobile = new Phrase("" + Globals.objLPR.getMobile_No(), B10);

                }
                PdfPCell cell_company_mobile;

                cell_company_mobile = new PdfPCell(prcommpanymobile);
                cell_company_mobile.setBorder(Rectangle.NO_BORDER);
                // cell_company_add.setColspan(1);
                cell_company_mobile.setHorizontalAlignment(Element.ALIGN_CENTER);
                //cell_company_name.setPadding(5.0f);
                table_company_mobile.addCell(cell_company_mobile);

                PdfPTable table_posno = new PdfPTable(2);
                Phrase prposno = new Phrase(getString(R.string.POS_No), B10);
                PdfPCell cell_posno = new PdfPCell(prposno);
                cell_posno.setBorder(Rectangle.NO_BORDER);

                //  cell_posno.setColspan(1);
                cell_posno.setHorizontalAlignment(Element.ALIGN_LEFT);

                table_posno.addCell(cell_posno);
                prposno = new Phrase("" + Globals.objLPD.getDevice_Code(), B10);
                PdfPCell possecondcolumn = new PdfPCell(prposno);
                possecondcolumn.setBorder(Rectangle.NO_BORDER);

                possecondcolumn.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_posno.addCell(possecondcolumn);
                document.open();

                PdfPTable table_order_no = new PdfPTable(2);
                Phrase prorderno = new Phrase(getString(R.string.slip_no), B10);
                PdfPCell cell_order_no = new PdfPCell(prorderno);
                cell_order_no.setBorder(Rectangle.NO_BORDER);

                // cell_order_no.setColspan(1);
                cell_order_no.setHorizontalAlignment(Element.ALIGN_LEFT);

                table_order_no.addCell(cell_order_no);
                prorderno = new Phrase("" + orders.get_order_code(), B10);
                PdfPCell ordercol = new PdfPCell(prorderno);

                ordercol.setBorder(Rectangle.NO_BORDER);
                ordercol.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_order_no.addCell(ordercol);
                document.open();


                PdfPTable table_order_date = new PdfPTable(2);
                if(orders.get_table_code().length()>0) {
                    Phrase prorderdate = new Phrase(getString(R.string.vehicleno), B10);
                    PdfPCell cell_order_date = new PdfPCell(prorderdate);
                    cell_order_date.setBorder(Rectangle.NO_BORDER);

                    // cell_order_date.setColspan(1);
                    cell_order_date.setHorizontalAlignment(Element.ALIGN_LEFT);

                    table_order_date.addCell(cell_order_date);
                    prorderdate = new Phrase("" + orders.get_table_code(), B10);
                    PdfPCell ordercoldate = new PdfPCell(prorderdate);
                    ordercoldate.setBorder(Rectangle.NO_BORDER);


                    ordercoldate.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_order_date.addCell(ordercoldate);

                    document.open();
                }
                Contact contact = Contact.getContact(getActivity(), database, db, " WHERE contact_code='" + orders.get_contact_code() + "'");

                PdfPTable table_order_time = new PdfPTable(2);
                if(contact.get_contact_1().length()>0) {
                    Phrase prordertime = new Phrase(getString(R.string.Mobile_No), B10);
                    PdfPCell cell_order_time = new PdfPCell(prordertime);
                    cell_order_time.setBorder(Rectangle.NO_BORDER);

                    // cell_order_date.setColspan(1);
                    cell_order_time.setHorizontalAlignment(Element.ALIGN_LEFT);

                    table_order_time.addCell(cell_order_time);
                    prordertime = new Phrase("" +  contact.get_contact_1(), B10);
                    PdfPCell ordercoltime = new PdfPCell(prordertime);
                    ordercoltime.setBorder(Rectangle.NO_BORDER);
                    ordercoltime.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_order_time.addCell(ordercoltime);
                    document.open();
                }
//                User user = User.getUser(getActivity(), " Where user_code='" + Globals.user + "'", database);
                PdfPTable table_cashier = new PdfPTable(2);
                Phrase prordercashier = new Phrase(getString(R.string.vehicletype), B10);
                PdfPCell cell_cashier = new PdfPCell(prordercashier);
                cell_cashier.setBorder(Rectangle.NO_BORDER);

                // cell_cashier.setColspan(1);
                cell_cashier.setHorizontalAlignment(Element.ALIGN_LEFT);

                table_cashier.addCell(cell_cashier);
                prordercashier = new Phrase("" + orders.get_remarks(), B10);
                PdfPCell ordercolcashier = new PdfPCell(prordercashier);

                ordercolcashier.setBorder(Rectangle.NO_BORDER);

                ordercolcashier.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_cashier.addCell(ordercolcashier);
                document.open();

                PdfPTable table_customer = new PdfPTable(2);


                    Phrase prordercustomer = new Phrase(getString(R.string.indate), B10);

                    PdfPCell cell_customer = new PdfPCell(prordercustomer);
                    cell_customer.setBorder(Rectangle.NO_BORDER);
                    //cell_customer.setColspan(1);
                    cell_customer.setHorizontalAlignment(Element.ALIGN_LEFT);

                    table_customer.addCell(cell_customer);
                    prordercustomer = new Phrase("" + DateUtill.PaternDate1(orders.get_order_date()).substring(0,11).toString(), B10);
                    PdfPCell ordercolcustomer = new PdfPCell(prordercustomer);

                    ordercolcustomer.setBorder(Rectangle.NO_BORDER);
                    ordercolcustomer.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_customer.addCell(ordercolcustomer);
                    document.open();

                PdfPTable table_subtotal = new PdfPTable(2);
                PdfPTable table_total_tax = new PdfPTable(2);
                PdfPTable table_discount = new PdfPTable(2);
                PdfPTable table_net_amount = new PdfPTable(2);
                PdfPTable table_tender = new PdfPTable(2);
                if(strflagbtn.equals("VehicleIn")) {
                    if (orders.get_order_status().equals("CLOSE")) {



                        Phrase pr23 = new Phrase(getString(R.string.intime), B10);
                        PdfPCell c16 = new PdfPCell(pr23);
                        //c16.setPadding(5);
                        c16.setBorder(Rectangle.NO_BORDER);

                        c16.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table_subtotal.addCell(c16);
                        pr23 = new Phrase("" + orders.get_order_date().substring(11, 19).toString(), B10);

                        PdfPCell c17 = new PdfPCell(pr23);
                        //  c17.setPadding(5);
                        c17.setBorder(Rectangle.NO_BORDER);

                        c17.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table_subtotal.addCell(c17);
                        table_subtotal.setSpacingBefore(10.0f);
                        document.open();




                        Phrase pr234 = new Phrase(getString(R.string.outym), B10);
                        PdfPCell c161 = new PdfPCell(pr234);
                        // c161.setPadding(5);
                        c161.setBorder(Rectangle.NO_BORDER);

                        c161.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table_total_tax.addCell(c161);
                        pr234 = new Phrase("" + orders.get_delivery_date().substring(11, 19), B10);

                        PdfPCell c173 = new PdfPCell(pr234);
                        //  c173.setPadding(5);
                        c173.setBorder(Rectangle.NO_BORDER);

                        c173.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table_total_tax.addCell(c173);
//            table_total_tax.setSpacingBefore(10.0f);
                        document.open();
                    } else {


                        Phrase pr24 = new Phrase(getString(R.string.intime), B10);
                        PdfPCell c11 = new PdfPCell(pr24);
                        //   c11.setPadding(5);
                        c11.setBorder(Rectangle.NO_BORDER);

                        c11.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table_discount.addCell(c11);
                        pr24 = new Phrase("" + orders.get_order_date().substring(11, 19), B10);

                        PdfPCell c19 = new PdfPCell(pr24);
                        // c19.setPadding(5);
                        c19.setBorder(Rectangle.NO_BORDER);

                        c19.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table_discount.addCell(c19);
                        // table_discount.setSpacingBefore(10.0f);
                        document.open();
                    }



                    Phrase pr248 = new Phrase(getString(R.string.advanceamnt), B10);
                    PdfPCell c171 = new PdfPCell(pr248);
                    //  c171.setPadding(5);
                    c171.setBorder(Rectangle.NO_BORDER);

                    c171.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_net_amount.addCell(c171);
                    pr248 = new Phrase("" + Globals.myNumberFormat2Price(Double.parseDouble(order_detail1.get_discount()), decimal_check), B10);

                    PdfPCell c169 = new PdfPCell(pr248);
                    //c169.setPadding(5);
                    c169.setBorder(Rectangle.NO_BORDER);

                    c169.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_net_amount.addCell(c169);
//            table_net_amount.setSpacingBefore(10.0f);
                    document.open();

                }
                else if(strflagbtn.equals("checkout")) {


                    Phrase pr249 = new Phrase(getString(R.string.outym), B10);
                    PdfPCell c178 = new PdfPCell(pr249);
                    //c178.setPadding(5);
                    c178.setBorder(Rectangle.NO_BORDER);

                    c178.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_tender.addCell(c178);
                    pr249 = new Phrase("" + orders.get_delivery_date().substring(11, 19), B10);

                    PdfPCell c167 = new PdfPCell(pr249);
                    // c167.setPadding(5);
                    c167.setBorder(Rectangle.NO_BORDER);

                    c167.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_tender.addCell(c167);
//            table_tender.setSpacingBefore(10.0f);
                    document.open();
                }

                PdfPTable table_change = new PdfPTable(2);

                Phrase pr242 = new Phrase(getString(R.string.fareamnt), B10);
                PdfPCell c158 = new PdfPCell(pr242);
                // c158.setPadding(5);
                c158.setBorder(Rectangle.NO_BORDER);

                c158.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_change.addCell(c158);
                pr242 = new Phrase("" + Globals.myNumberFormat2Price(Double.parseDouble(orders.get_total()), decimal_check), B10);

                PdfPCell c187 = new PdfPCell(pr242);
                // c187.setPadding(5);
                c187.setBorder(Rectangle.NO_BORDER);

                c187.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_change.addCell(c187);
//            table_change.setSpacingBefore(10.0f);
                document.open();


                PdfPTable table_collectamnt = new PdfPTable(2);
                if(strflagbtn.equals("checkout")) {

                    Phrase prcamnt = new Phrase(getString(R.string.collectamnt), B10);
                    PdfPCell c1amnt = new PdfPCell(prcamnt);
                    // c158.setPadding(5);
                    c1amnt.setBorder(Rectangle.NO_BORDER);

                    c1amnt.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_collectamnt.addCell(c1amnt);
                    prcamnt = new Phrase("" + Globals.myNumberFormat2Price(Double.parseDouble(order_payment.get_pay_amount()), decimal_check), B10);

                    PdfPCell c1camnt = new PdfPCell(prcamnt);
                    // c187.setPadding(5);
                    c1camnt.setBorder(Rectangle.NO_BORDER);

                    c1camnt.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table_collectamnt.addCell(c1camnt);
//            table_change.setSpacingBefore(10.0f);
                    document.open();

                }
                PdfPTable table_vehstatus = new PdfPTable(2);

                Phrase prcvehstatus = new Phrase(getString(R.string.vehiclestatus), B10);
                PdfPCell c1vehst= new PdfPCell(prcvehstatus);
                // c158.setPadding(5);
                c1vehst.setBorder(Rectangle.NO_BORDER);

                c1vehst.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_vehstatus.addCell(c1vehst);
                prcvehstatus = new Phrase("" + orders.get_order_status() , B10);

                PdfPCell cvehstatusp = new PdfPCell(prcvehstatus);
                // c187.setPadding(5);
                cvehstatusp.setBorder(Rectangle.NO_BORDER);

                cvehstatusp.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_vehstatus.addCell(cvehstatusp);
//            table_change.setSpacingBefore(10.0f);
                document.open();



                document.add(tableh);
                document.add(table_company_name);
                document.add(table_company_adres);
                document.add(table_company_mobile);
                document.add(table_posno);
                document.add(table_order_no);
                document.add(table_order_date);
                document.add(table_order_time);
                document.add(table_cashier);
                document.add(table_customer);

                document.add(table_subtotal);
                document.add(table_total_tax);
                document.add(table_discount);
                document.add(table_net_amount);
                document.add(table_tender);
                document.add(table_change);
                if(strflagbtn.equals("checkout")) {
                    document.add(table_collectamnt);
                }
                document.add(table_vehstatus);

                document.newPage();
                document.close();
                file.close();
            }



 /*           Toast.makeText(getActivity(), "Pdf Created Successfully",
                    Toast.LENGTH_SHORT).show();*/
            if (f.exists()) {
                Uri path = Uri.fromFile(f);
                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(path, "application/pdf");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                try {
                } catch (ActivityNotFoundException e) {
                }
            }
            // }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    void resolveIntent(String TagId) {
/*        Context context = getActivity();


        // 1) Parse the intent and get the action that triggered this intent
        String action = intent.getAction();

        // 2) Check if it was triggered by a tag discovered interruption.
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            //  3) Get an instance of the TAG from the NfcAdapter
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] extraID = tagFromIntent.getId();

            StringBuilder sb = new StringBuilder();
            for (byte b : extraID) {
                sb.append(String.format("%02X", b));
            };

            String tagID = sb.toString();
            Log.e("nfc ID", tagID);*/
        edt_nfc.setVisibility(View.VISIBLE);
            edt_nfc.setText(TagId);

       /* }else{

            showAlert("Do not find a card");
        }*/

    }// End of method

    private void showAlert(String alertCase) {
        // prepare the alert box
        android.app.AlertDialog.Builder alertbox = new AlertDialog.Builder(getActivity());

        alertbox.setMessage(alertCase);
        // set a positive/yes button and create a listener
        alertbox.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            // Save the data from the UI to the database - already done
            public void onClick(DialogInterface arg0, int arg1) {
                //clearFields();
            }
        });
        // display box
        alertbox.show();
    }

  /*  @Override
    public void onResume() {
        super.onResume();

        //-----------------非常关键，必要的哦，不能删除----------------
        if(Globals.objsettings.getIs_NFC().equals("true")) {
            if(nfcAdapter!=null) {
                nfcAdapter.enableForegroundDispatch(getActivity(), mPendingIntent, mFilters,
                        mTechLists);
                if(strTagId!=null) {
                    resolveIntent(strTagId);
                }
            }
            else{
               // Toast.makeText(getActivity(),"NFC is not enabled",Toast.LENGTH_LONG).show();

            }
        }
    }*/

    public class generateRandom
    {
        public  void main(String args[])
        {
            Random rand = new Random();

            // Generate random integers in range 0 to 999
            int rand_int1 = rand.nextInt(1000);
            int rand_int2 = rand.nextInt(1000);

            // Print random integers
            System.out.println("Random Integers: " + rand_int1);
            System.out.println("Random Integers: " + rand_int2);
        }
    }

    void resolveIntent(Intent intent) {
        Context context = getActivity();


        // 1) Parse the intent and get the action that triggered this intent
        String action = intent.getAction();

        // 2) Check if it was triggered by a tag discovered interruption.
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            //  3) Get an instance of the TAG from the NfcAdapter
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] extraID = tagFromIntent.getId();

            StringBuilder sb = new StringBuilder();
            for (byte b : extraID) {
                sb.append(String.format("%02X", b));
            };

            String tagID = sb.toString();
            //edt_nfc.setText(tagID);
           /* Bundle bundle = new Bundle();
            bundle.putParcelable("TagId", tagFromIntent);
// set Fragmentclass Arguments
            VehicleIN_Fragment fragobj = new VehicleIN_Fragment();
            fragobj.setArguments(bundle);*/
            Log.e("nfc ID", tagID);
            //tv_nfctag.setText(tagID);

        }else{

            showAlert("Do not find a card");
        }

    }// End of method


    public void checkNfcTag_Orderstatus(){


    }

    private void showdialog(String strFlag) {
        listDialog = new Dialog(getActivity());
listDialog.setTitle("Choose Option");
        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.customdialog, null, false);
        listDialog.setContentView(v);
        listDialog.setCancelable(false);
        final Button btnprint = (Button) listDialog.findViewById(R.id.btn_print);
        final Button btnwhatsappshare = (Button) listDialog.findViewById(R.id.btn_whatsappShare);
        final Button btnclose = (Button) listDialog.findViewById(R.id.btn_close);
        btnwhatsappshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runthread1(strFlag);
              //  listDialog.dismiss();
            }
        });
        btnprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_remainingcode(strFlag);
                listDialog.dismiss();
            }
        });
        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDialog.dismiss();
               edt_mobileno.setText("");
               edt_nfc.setText("");
               edt_vehicleno.setText("");
               edt_advanceAmnt.setText("");
               rd_btn2w.setChecked(false);
                rd_btn3w.setChecked(false);
                rd_btn4w.setChecked(false);
                rd_btnstaff.setChecked(false);
                tv_chargeamnt.setText("");
                Globals.setEmpty();
            }
        });

        listDialog.show();
    }
}
