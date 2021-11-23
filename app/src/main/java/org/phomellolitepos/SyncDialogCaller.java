package org.phomellolitepos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.core.content.IntentCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Address;
import org.phomellolitepos.database.Address_Lookup;
import org.phomellolitepos.database.Bussiness_Group;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Contact_Bussiness_Group;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Item_Supplier;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Order_Type_Tax;
import org.phomellolitepos.database.Payment;
import org.phomellolitepos.database.ReceipeModifier;
import org.phomellolitepos.database.Returns;
import org.phomellolitepos.database.ScaleSetup;
import org.phomellolitepos.database.Sys_Sycntime;
import org.phomellolitepos.database.Sys_Tax_Group;
import org.phomellolitepos.database.Table;
import org.phomellolitepos.database.Tax_Detail;
import org.phomellolitepos.database.Tax_Master;
import org.phomellolitepos.database.Unit;
import org.phomellolitepos.database.User;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SyncDialogCaller{
    ProgressDialog progressDialog;
    Context context;
    SQLiteDatabase database;
    Item_Location item_location;
    Item_Group item_group;
    Item_Group_Tax item_group_tax;
    ReceipeModifier item_modifier;
    Tax_Detail tax_detail;
    Tax_Master tax_master;
    Order_Type_Tax order_type_tax;
    Tax_Detail taxItem_detail;
    Contact contact;
    Address address;
    Unit unit;
     Boolean bflag=false;
    Address_Lookup address_lookup;
    Contact_Bussiness_Group contact_bussiness_group;
    Database db;
    User user;
    Returns returns;
    Item item;
    ProgressDialog pDialog;
    Item_Supplier item_supplier;
    public SyncDialogCaller(Context ctx,SQLiteDatabase sqldb,Database db1) {
        this.context=ctx;
        this.database=sqldb;
        this.db=db1;

    }



    public static void showDialog(Context context, String title, String message,
                                  DialogInterface.OnClickListener onClickListener) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton("Ok", onClickListener);
        dialog.setNegativeButton("Cancel", null);
        dialog.show();
    }

    public void sync_all(ProgressDialog pDialog,final Context ctx, SQLiteDatabase database,String serialno,String
                          android, String mykey,String liccustid){


                        try {

                            String result;
                            String suss;


                            try {
                                // result = send_online_item_group();
                                Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(context, database, db, "WHERE table_name ='item_group'");

                                if (sys_sycntime == null) {
                                    getitemgrp_from_server(pDialog,"", serialno, android, mykey);
                                } else {
                                    getitemgrp_from_server(pDialog,sys_sycntime.get_datetime(), serialno, android, mykey);
                                }

                            } catch (Exception ex) {
                            }





                          /*  try {
                                result = send_online_item();
                                suss = getitem();
                            } catch (Exception ex) {
                            }
*/

                            // call Item Location Stock Same Logic Copy Here
                           /* try {

                                suss = getLocationStock();
                            } catch (Exception e) {

                            }

                            try {
                                result = send_online_tax();
                                suss = getTax();
                            } catch (Exception ex) {
                            }

                            try {
                                send_online_user();
                                suss = getUser();
                            } catch (Exception ex) {
                            }



                            try{
                                result = send_online_unit();
                                suss = getUnit();
                            }
                            catch(Exception e){

                            }*/
//                                    try {
//                                        result = Orders.sendOnServer(getApplicationContext(), database, db, "Select * From orders WHERE is_push = 'N'",liccustomerid,serial_no,android_id,myKey);
//
//
//
//
//                                    } catch (Exception ex) {
//                                    }



                        } catch (final Exception e) {


                            Toast.makeText(ctx, R.string.somthing_wnt_wrng, Toast.LENGTH_SHORT).show();

                        }


    }


    private String getBussinessGroup(String serverData) {
        String succ_manu = "0";
        // Call get bussiness group api here
        Bussiness_Group bussiness_group;
        database.beginTransaction();

        try {
            final JSONObject jsonObject_bp = new JSONObject(serverData);
            final String strStatus = jsonObject_bp.getString("status");
            if (strStatus.equals("true")) {
                JSONArray jsonArray_bg = jsonObject_bp.getJSONArray("result");
                for (int i = 0; i < jsonArray_bg.length(); i++) {
                    JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                    String bg_code = jsonObject_bg1.getString("business_group_code");
                    String bg_id = jsonObject_bg1.getString("business_group_id");
                    bussiness_group = Bussiness_Group.getBussiness_Group(context, database, db, "WHERE business_group_code ='" + bg_code + "'");
                    if (bussiness_group == null) {
                        bussiness_group = new Bussiness_Group(context, null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("business_group_code"), jsonObject_bg1.getString("parent_code"), jsonObject_bg1.getString("name"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y");
                        long l = bussiness_group.insertBussiness_Group(database);
                        if (l > 0) {
                            succ_manu = "1";
                        }
                    } else {
                        bussiness_group = new Bussiness_Group(context, bg_id, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("business_group_code"), jsonObject_bg1.getString("parent_code"), jsonObject_bg1.getString("name"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y");
                        long l = bussiness_group.updateBussiness_Group("business_group_code=? And business_group_id=? ", new String[]{bg_code, bg_id}, database);
                        if (l > 0) {
                            succ_manu = "1";
                        }
                    }
                }
            } else {
            }

            if (succ_manu.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.endTransaction();
            }

        } catch (JSONException e) {
            succ_manu = "3";
            database.endTransaction();
        }
        return succ_manu;
    }

   /* private String get_bussiness_gp_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                 Globals.App_IP_URL +"business_group");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("company_id", Globals.Company_Id));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            serverData = EntityUtils.toString(httpEntity);
            Log.d("response", serverData);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;

    }*/

    private String send_online_bussiness() {
        String l = Bussiness_Group.sendOnServer(context, database, db, "Select  * From business_group  WHERE is_push = 'N'");
        return l;
    }

    private String send_online_item_group(final ProgressDialog pDialog) {
        //Globals.reg_code = lite_pos_registration.getRegistration_Code();
        String l = Item_Group.sendOnServer(pDialog,context, database, db, "Select * From item_group  WHERE is_push = 'N'", Globals.serialno, Globals.syscode2, Globals.androidid, Globals.mykey, Globals.objLPR.getLicense_No());
        return l;
    }

    private String getitemGroup(String serverData) {

        Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(context, database, db, "WHERE table_name ='item_group'");
        String succ_bg = "0";

        // Call get item group api here
        System.out.println("get sync date" + sys_sycntime.get_datetime());
        database.beginTransaction();


        try {
            final JSONObject jsonObject_bg = new JSONObject(serverData);
            final String strStatus = jsonObject_bg.getString("status");
            final String strmessage = jsonObject_bg.getString("message");
            if (strStatus.equals("true")) {
                JSONArray jsonArray_bg = jsonObject_bg.getJSONArray("result");
                for (int i = 0; i < jsonArray_bg.length(); i++) {
                    JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                    String item_group_code = jsonObject_bg1.getString("item_group_code");
                    item_group = Item_Group.getItem_Group(context, database, db, "WHERE item_group_code ='" + item_group_code + "'");

                    if (sys_sycntime != null) {
                        sys_sycntime.set_datetime(jsonObject_bg1.getString("modified_date"));
                        long l1 = sys_sycntime.updateSys_Sycntime("table_name=?", new String[]{"item_group"}, database);
                    }

                    if (item_group == null) {
                        item_group = new Item_Group(context, null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("parent_code"), jsonObject_bg1.getString("item_group_name"), "0", jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y",jsonObject_bg1.getString("printer_ip"));
                        long l = item_group.insertItem_Group(database);
                        if (l > 0) {
                            succ_bg = "1";
                        } else {
                        }
                    } else {
                        item_group = new Item_Group(context, item_group.get_item_group_id(), jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("parent_code"), jsonObject_bg1.getString("item_group_name"), "0", jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y",jsonObject_bg1.getString("printer_ip"));
                        long l = item_group.updateItem_Group("item_group_code=?", new String[]{item_group_code}, database);
                        if (l > 0) {
                            succ_bg = "1";
                        } else {
                        }
                    }
                }
            } else if (strStatus.equals("false")) {

                succ_bg = "3";
                Globals.responsemessage = strmessage;


            }

            if (succ_bg.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.endTransaction();
            }
        } catch (JSONException e) {
            succ_bg = "2";

            database.endTransaction();
        }
        return succ_bg;
    }
    private String getinvoiceparameter(String serverData) {
        String succ_manu = "0";
        // Call get bussiness group api here
      Lite_POS_Registration  lite_pos_registration = Lite_POS_Registration.getRegistration(context, database, db, "");

        database.beginTransaction();

        try {
            final JSONObject jsonObject_bp = new JSONObject(serverData);
            final String strStatus = jsonObject_bp.getString("status");
            if (strStatus.equals("true")) {

                JSONObject jsonobj_bg = jsonObject_bp.getJSONObject("result");
                String gst_number=jsonobj_bg.getString("gst_number");
                String inv_address=jsonobj_bg.getString("invoice_address");
                String shortcompanyname=jsonobj_bg.getString("company_name");
                String industry_type=jsonobj_bg.getString("industry_type");
                String locationid=jsonobj_bg.getString("location_id");
                String locationname=jsonobj_bg.getString("location_name");

                Globals.objLPD.setLocation_Code(locationid);
                Globals.objLPD.setLocation_name(locationname);
                long dt = Globals.objLPD.updateDevice("Id=?", new String[]{"1"}, database);
                if (dt > 0) {

                }
                lite_pos_registration.setAddress(inv_address);
                lite_pos_registration.setService_code_tariff(gst_number);
                lite_pos_registration.setShort_companyname(shortcompanyname);
                  lite_pos_registration.setIndustry_Type(industry_type);
                long ct = lite_pos_registration.updateRegistration("Id=?", new String[]{"1"}, database);
                if(ct>0){
                    succ_manu="1";
                }
                /*if(Globals.objLPR.getIndustry_Type().equals(industry_type)){
                    succ_manu="1";
                }
                else{
                    succ_manu="2";
                }*/

            } else {
                database.endTransaction();
            }
            if (succ_manu.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else if (succ_manu.equals("2")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            }
            else{
                database.endTransaction();
            }
        } catch (JSONException e) {
            succ_manu = "3";
            database.endTransaction();
        }
        return succ_manu;
    }


   /* private String getInvoiceParameter_fromserver(String serial_no,String android_id,String myKey,String liccustomerid) {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(Globals.App_IP_URL + "invoice_parameter");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", serial_no));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", android_id));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", myKey));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", liccustomerid));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            serverData = EntityUtils.toString(httpEntity);
            Log.d("response", serverData);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;
    }*/
   /* private String get_item_gp_from_server(String datetime,String serial_no,String android_id,String myKey,String liccustomerid) {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "item_group");
        ArrayList nameValuePairs = new ArrayList(8);
        nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("modified_date", datetime));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", serial_no));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", android_id));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", myKey));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", liccustomerid));
        System.out.println("namevalue get group" + nameValuePairs);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            serverData = EntityUtils.toString(httpEntity);
            Log.d("response", serverData);
            System.out.println("response get group " + serverData);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;


    }
*/
    private JSONObject send_online_contact(String serial_no,String android_id,String myKey) {

        JSONObject conList = Contact.sendOnServer(context, database, db, "Select device_code, contact_code,title,name,gender,dob,company_name,description,contact_1,contact_2,email_1,email_2,is_active,modified_by,credit_limit,gstin,country_id,zone_id from contact where is_push='N'", Globals.objLPR.getLicense_No(), serial_no, android_id, myKey);
        return conList;
    }

    private String getContact(String serverData) {

        Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(context, database, db, "WHERE table_name='contact'");

        String succ_bg = "0";
        database.beginTransaction();
        //Call get contact api here

        try {
            final JSONObject jsonObject_contact = new JSONObject(serverData);
            final String strStatus = jsonObject_contact.getString("status");
            final String strmsg = jsonObject_contact.getString("message");
            if (strStatus.equals("true")) {

                JSONArray jsonArray_contact = jsonObject_contact.getJSONArray("result");
                for (int i = 0; i < jsonArray_contact.length(); i++) {
                    JSONObject jsonObject_contact1 = jsonArray_contact.getJSONObject(i);
                    String contact_code = jsonObject_contact1.getString("contact_code");
                    contact = Contact.getContact(context, database, db, "WHERE contact_code ='" + contact_code + "'");
                    if (sys_sycntime!=null){
                        sys_sycntime.set_datetime(jsonObject_contact1.getString("modified_date"));
                        long l1 = sys_sycntime.updateSys_Sycntime("table_name=?", new String[]{"contact"}, database);
                    }


                    if (contact == null) {
                        contact = new Contact(context, null, jsonObject_contact1.getString("device_code"), jsonObject_contact1.getString("contact_code"), jsonObject_contact1.getString("title"), jsonObject_contact1.getString("name"), jsonObject_contact1.getString("gender"), jsonObject_contact1.getString("dob"), jsonObject_contact1.getString("company_name"), jsonObject_contact1.getString("description"), jsonObject_contact1.getString("contact_1"), jsonObject_contact1.getString("contact_2"), jsonObject_contact1.getString("email_1"), jsonObject_contact1.getString("email_2"), jsonObject_contact1.getString("is_active"), jsonObject_contact1.getString("modified_by"), "Y", "0", jsonObject_contact1.getString("modified_date"), "0", jsonObject_contact1.getString("gstin"), jsonObject_contact1.getString("country_id"), jsonObject_contact1.getString("zone_id"),jsonObject_contact1.getString("is_taxable"));
                        long l = contact.insertContact(database);
                        if (l > 0) {
                            succ_bg = "1";
                            JSONArray json_item_address = jsonObject_contact1.getJSONArray("address");

                            for (int j = 0; j < json_item_address.length(); j++) {
                                JSONObject jsonObject_address = json_item_address.getJSONObject(j);
                                address = new Address(context, null, jsonObject_address.getString("device_code"), jsonObject_address.getString("address_code"), jsonObject_address.getString("address_category_code"), jsonObject_address.getString("area_id"), jsonObject_address.getString("address"), jsonObject_address.getString("landmark"), jsonObject_address.getString("latitude"), jsonObject_address.getString("longitude"), jsonObject_address.getString("contact_person"), jsonObject_address.getString("contact"), jsonObject_address.getString("is_active"), jsonObject_address.getString("modified_by"), jsonObject_address.getString("modified_date"), "Y");
                                long itmsp = address.insertAddress(database);

                                if (itmsp > 0) {
                                    succ_bg = "1";

                                } else {
                                    succ_bg = "0";
                                }


                            }

                            JSONArray json_address_lookup = jsonObject_contact1.getJSONArray("address_lookup");

                            for (int al = 0; al < json_address_lookup.length(); al++) {
                                JSONObject jsonObject_address_lookup = json_address_lookup.getJSONObject(al);
                                address_lookup = new Address_Lookup(context, null, jsonObject_address_lookup.getString("device_code"), jsonObject_address_lookup.getString("address_code"), jsonObject_address_lookup.getString("refrence_type"), jsonObject_address_lookup.getString("refrence_code"), "N");
                                long chk_ad_lookup = address_lookup.insertAddress_Lookup(database);

                                if (chk_ad_lookup > 0) {
                                    succ_bg = "1";

                                } else {
                                    succ_bg = "0";
                                }
                            }

                            JSONArray json_cbgp = jsonObject_contact1.getJSONArray("contact_business_group");

                            for (int cbgp = 0; cbgp < json_cbgp.length(); cbgp++) {
                                JSONObject jsonObject_cbgp = json_cbgp.getJSONObject(cbgp);
                                contact_bussiness_group = new Contact_Bussiness_Group(context, jsonObject_cbgp.getString("contact_code"), jsonObject_cbgp.getString("business_group_code"));
                                long chk_cbgp = contact_bussiness_group.insertContact_Bussiness_Group(database);

                                if (chk_cbgp > 0) {
                                    succ_bg = "1";
                                } else {
                                    succ_bg = "0";
                                }
                            }
                        } else {
                            succ_bg = "0";
                        }
                    } else {

                        // Edit on 18-Oct-2017
                        contact = new Contact(context, contact.get_contact_id(), jsonObject_contact1.getString("device_code"), jsonObject_contact1.getString("contact_code"), jsonObject_contact1.getString("title"), jsonObject_contact1.getString("name"), jsonObject_contact1.getString("gender"), jsonObject_contact1.getString("dob"), jsonObject_contact1.getString("company_name"), jsonObject_contact1.getString("description"), jsonObject_contact1.getString("contact_1"), jsonObject_contact1.getString("contact_2"), jsonObject_contact1.getString("email_1"), jsonObject_contact1.getString("email_2"), jsonObject_contact1.getString("is_active"), jsonObject_contact1.getString("modified_by"), "Y", "0", jsonObject_contact1.getString("modified_date"), "0", jsonObject_contact1.getString("gstin"), jsonObject_contact1.getString("country_id"), jsonObject_contact1.getString("zone_id"),jsonObject_contact1.getString("is_taxable"));

                        long l = contact.updateContact("contact_code=? And contact_id=?", new String[]{contact_code, contact.get_contact_id()}, database);

                        if (l > 0) {
                            succ_bg = "1";
                            JSONArray json_item_address = jsonObject_contact1.getJSONArray("address");

                            for (int j = 0; j < json_item_address.length(); j++) {
                                JSONObject jsonObject_address = json_item_address.getJSONObject(j);
                                address = Address.getAddress(context, "WHERE address_code ='" + contact_code + "'", database);
                                if (address != null) {
                                    address = new Address(context, address.get_address_id(), jsonObject_address.getString("device_code"), jsonObject_address.getString("address_code"), jsonObject_address.getString("address_category_code"), jsonObject_address.getString("area_id"), jsonObject_address.getString("address"), jsonObject_address.getString("landmark"), jsonObject_address.getString("latitude"), jsonObject_address.getString("longitude"), jsonObject_address.getString("contact_person"), jsonObject_address.getString("contact"), jsonObject_address.getString("is_active"), jsonObject_address.getString("modified_by"), jsonObject_address.getString("modified_date"), "Y");
                                    long itmsp = address.updateAddress("address_code=? And address_id=?", new String[]{contact_code, address.get_address_id()}, database);

                                    if (itmsp > 0) {
                                        succ_bg = "1";

                                    } else {
                                        succ_bg = "0";
                                    }
                                }
                            }

                            JSONArray json_address_lookup = jsonObject_contact1.getJSONArray("address_lookup");
                            long g = address_lookup.delete_Address_Lookup(context, "address_code=?", new String[]{contact_code}, database);
                            for (int al = 0; al < json_address_lookup.length(); al++) {
                                JSONObject jsonObject_address_lookup = json_address_lookup.getJSONObject(al);

                                address_lookup = Address_Lookup.getAddress_Lookup(context, "WHERE address_code ='" + contact_code + "'", database);


                                address_lookup = new Address_Lookup(context, null, jsonObject_address_lookup.getString("device_code"), jsonObject_address_lookup.getString("address_code"), jsonObject_address_lookup.getString("refrence_type"), jsonObject_address_lookup.getString("refrence_code"), "N");
                                long chk_ad_lookup = address_lookup.insertAddress_Lookup(database);

                                if (chk_ad_lookup > 0) {
                                    succ_bg = "1";

                                } else {
                                    succ_bg = "0";
                                }
                            }

                            JSONArray json_cbgp = jsonObject_contact1.getJSONArray("contact_business_group");
                            long c = contact_bussiness_group.delete_Contact_Bussiness_Group(context, database, db, "contact_code=?", new String[]{contact_code});
                            for (int cbgp = 0; cbgp < json_cbgp.length(); cbgp++) {
                                JSONObject jsonObject_cbgp = json_cbgp.getJSONObject(cbgp);
                                contact_bussiness_group = new Contact_Bussiness_Group(context, jsonObject_cbgp.getString("contact_code"), jsonObject_cbgp.getString("business_group_code"));
                                long chk_cbgp = contact_bussiness_group.insertContact_Bussiness_Group(database);

                                if (chk_cbgp > 0) {
                                    succ_bg = "1";
                                } else {
                                    succ_bg = "0";
                                }
                            }
                        } else {
                            succ_bg = "0";
                        }
                    }
                }
            }
            else if (strStatus.equals("false")) {

                succ_bg="3";
                Globals.responsemessage=strmsg;
            }




            else {
                succ_bg = "0";
            }

            if (succ_bg.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.endTransaction();
            }
        } catch (JSONException e) {
            succ_bg = "2";
            database.endTransaction();
        }
        return succ_bg;
    }



    public void getZone_Table_fromserver(final String serial_no,final String android_id,final String myKey) {

        /*pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.loggingin));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "dine_in";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result=getTableZone(response);
                        } catch (Exception e) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        //  pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("reg_code",Globals.objLPR.getRegistration_Code());
                params.put("sys_code_1", serial_no);
                params.put("sys_code_2", Globals.syscode2);
                params.put("sys_code_3", android_id);
                params.put("sys_code_4", myKey);
                params.put("device_code", Globals.Device_Code);
                params.put("location_id", Globals.objLPD.getLocation_Code());
                params.put("lic_customer_license_id", Globals.objLPD.getLic_customer_license_id());
                System.out.println("params zone" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    private String getTableZone(String serverData) {
        String succ_manu = "0";
        // Call get bussiness group api here
        database.beginTransaction();

        try {
            final JSONObject jsonObject_bp = new JSONObject(serverData);
            final String strStatus = jsonObject_bp.getString("status");
            if (strStatus.equals("true")) {
                JSONObject jsonobject_result = jsonObject_bp.getJSONObject("result");
                JSONArray jsonArray_zone = jsonobject_result.optJSONArray("zones");
                if (jsonArray_zone.length() > 0) {
                    Long tbl = Table.deleteTable(context, "tables", "", new String[]{}, database);
                    for (int i = 0; i < jsonArray_zone.length(); i++) {

                        JSONObject jsonObject_zone = jsonArray_zone.getJSONObject(i);
                        String zoneid = jsonObject_zone.getString("diz_id");
                        String zonename = jsonObject_zone.getString("diz_name");

                        JSONArray jsonArray_tables = jsonObject_zone.getJSONArray("tables");
                        for (int j = 0; j < jsonArray_tables.length(); j++) {
                            JSONObject jsonObject_table = jsonArray_tables.getJSONObject(j);
                            String isactive = jsonObject_table.getString("is_active");
                            String modifiedBy = jsonObject_table.getString("modified_by");
                            String modifieddate = jsonObject_table.getString("modified_date");
                            String tableid = jsonObject_table.getString("dit_id");
                            String tablename = jsonObject_table.getString("dit_name");

                            Table table = new Table(context, null, tableid,
                                    tablename, zoneid, zonename, isactive, modifiedBy, modifieddate);


                            long k = table.insertTable(database);
                            if (k > 0) {
                                succ_manu = "1";
                            }
                        }
                    }
                }
            }
            if (succ_manu.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.endTransaction();
            }
        } catch (
                JSONException e) {
            succ_manu = "3";
            database.endTransaction();
        }
        return succ_manu;
    }

   /* private String get_contact_from_server(String datetime,String serial_no,String android_id,String myKey) {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                 Globals.App_IP_URL + "contact");
        ArrayList nameValuePairs = new ArrayList(8);
        nameValuePairs.add(new BasicNameValuePair("reg_code",Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("modified_date", datetime));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", serial_no));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", android_id));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", myKey));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", Globals.objLPR.getLicense_No()));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            serverData = EntityUtils.toString(httpEntity);
            Log.d("response", serverData);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;
    }*/

    private String send_online_item() {
        String result = Item.sendOnServer(progressDialog,context, database, db, "Select device_code, item_code,parent_code,item_group_code,manufacture_code,item_name,description,sku,barcode,image,hsn_sac_code,item_type,unit_id,is_return_stockable,is_service,is_active,modified_by,is_inclusive_tax FROM item  WHERE is_push = 'N'", Globals.objLPR.getLicense_No());
        return result;
    }

    private String getitem(String serverData) {

        Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(context, database, db, "WHERE table_name='item'");
        String succ_bg = "0";
        // Call get item api here
        database.beginTransaction();


        try {
            final JSONObject jsonObject_bg = new JSONObject(serverData);
            final String strStatus = jsonObject_bg.getString("status");
            final String strmsg = jsonObject_bg.getString("message");
            if (strStatus.equals("true")) {
                JSONArray jsonArray_bg = jsonObject_bg.getJSONArray("result");
                ArrayList<Item> itlist = new ArrayList<Item>();
                for (int i = 0; i < jsonArray_bg.length(); i++) {
                    JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                    String item_id = jsonObject_bg1.getString("item_id");
                    String item_code = jsonObject_bg1.getString("item_code");
                    item = Item.getItem(context, "WHERE item_code ='" + item_code + "'", database, db);
                    if (sys_sycntime != null) {
                        sys_sycntime.set_datetime(jsonObject_bg1.getString("modified_date"));
                        long l1 = sys_sycntime.updateSys_Sycntime("table_name=?", new String[]{"item"}, database);
                    }

                    String strImage = "", path = "";
                    try {
                        strImage = jsonObject_bg1.getString("image");
                        Uri myUri = Uri.parse(strImage);
                        path = getPath(context, myUri);
                    } catch (Exception ex) {
                    }
                    if (item == null) {
                       // itlist.add(new Item(context, null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_code"), "1", jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("manufacture_code"), jsonObject_bg1.getString("item_name"), jsonObject_bg1.getString("description"), jsonObject_bg1.getString("sku"), jsonObject_bg1.getString("barcode"), jsonObject_bg1.getString("hsn_sac_code"), path, jsonObject_bg1.getString("item_type"), jsonObject_bg1.getString("unit_id"), jsonObject_bg1.getString("is_return_stockable"), jsonObject_bg1.getString("is_service"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y", jsonObject_bg1.getString("is_inclusive_tax"), null));
                        item = new Item(context, null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_code").toString().trim(), "1", jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("manufacture_code"), jsonObject_bg1.getString("item_name").toString().trim(), jsonObject_bg1.getString("description"), jsonObject_bg1.getString("sku").toString().trim(), jsonObject_bg1.getString("barcode").toString().trim(), jsonObject_bg1.getString("hsn_sac_code").toString().trim(), path, jsonObject_bg1.getString("item_type"), jsonObject_bg1.getString("unit_id"), jsonObject_bg1.getString("is_return_stockable"), jsonObject_bg1.getString("is_service"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y", jsonObject_bg1.getString("is_inclusive_tax"), null,jsonObject_bg1.getString("is_modifier"));
                        long l = item.insertItem(database);

                        if (l > 0) {
                            succ_bg = "1";
                        } else {
                        }
                        /*try {
                            item.add_item(itlist, database);
                        } catch (Exception e) {

                        }*/
                        JSONArray json_item_location = jsonObject_bg1.getJSONArray("item_location");

                        for (int j = 0; j < json_item_location.length(); j++) {
                            JSONObject jsonObject_item_location = json_item_location.getJSONObject(j);
                            item_location = new Item_Location(context, null, jsonObject_item_location.getString("location_id"), jsonObject_item_location.getString("item_code"), jsonObject_item_location.getString("cost_price"), jsonObject_item_location.getString("markup"), jsonObject_item_location.getString("selling_price"), jsonObject_item_location.getString("quantity"), jsonObject_item_location.getString("loyalty_point"), jsonObject_item_location.getString("reorder_point"), jsonObject_item_location.getString("reorder_amount"), jsonObject_item_location.getString("is_inventory_tracking"), jsonObject_item_location.getString("is_active"), jsonObject_item_location.getString("modified_by"), jsonObject_item_location.getString("modified_date"), jsonObject_item_location.getString("new_sell_price"));
                            long itmlc = item_location.insertItem_Location(database);

                            if (itmlc > 0) {
                                succ_bg = "1";

                            } else {
                            }
                        }




                        JSONArray json_item_supplier = jsonObject_bg1.getJSONArray("item_supplier");

                        for (int k = 0; k < json_item_supplier.length(); k++) {
                            JSONObject jsonObject_item_supplier = json_item_supplier.getJSONObject(k);
                            item_supplier = new Item_Supplier(context, null, jsonObject_item_supplier.getString("item_code"), jsonObject_item_supplier.getString("contact_code"));
                            Log.i("qry", item_supplier.get_item_code() + "                 " + item_supplier.get_contact_code());
                            long itmsp = item_supplier.insertItem_Supplier(database);

                            if (itmsp > 0) {
                                succ_bg = "1";
                            } else {
                            }
                        }

                        JSONArray json_item_tax = jsonObject_bg1.getJSONArray("item_tax");

                        for (int it = 0;it< json_item_tax.length(); it++) {
                            JSONObject jsonObject_item_tax = json_item_tax.getJSONObject(it);
                            item_group_tax = new Item_Group_Tax(context, jsonObject_item_tax.getString("location_id"), jsonObject_item_tax.getString("tax_id"), jsonObject_item_tax.getString("item_group_code"));
                            long itmsp = item_group_tax.insertItem_Group_Tax(database);

                            if (itmsp > 0) {
                                succ_bg = "1";
                            } else {
                            }
                        }

                        JSONArray json_item_modifier = jsonObject_bg1.getJSONArray("item_modifier");

                        for (int j = 0; j < json_item_modifier.length(); j++) {
                            JSONObject jsonObject_item_modifier = json_item_modifier.getJSONObject(j);
                            item_modifier = new ReceipeModifier(context, null, jsonObject_item_modifier.getString("item_code"), jsonObject_item_modifier.getString("modifier_code"));
                            long itmlc = item_modifier.insertReceipemodifier(database);

                            if (itmlc > 0) {
                                succ_bg = "1";

                            } else {
                            }
                        }


                    } else {
                        item = new Item(context, item.get_item_id(), jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_code"), "1", jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("manufacture_code"), jsonObject_bg1.getString("item_name"), jsonObject_bg1.getString("description"), jsonObject_bg1.getString("sku"), jsonObject_bg1.getString("barcode"), jsonObject_bg1.getString("hsn_sac_code"), path, jsonObject_bg1.getString("item_type"), jsonObject_bg1.getString("unit_id"), jsonObject_bg1.getString("is_return_stockable"), jsonObject_bg1.getString("is_service"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y", jsonObject_bg1.getString("is_inclusive_tax"),null,jsonObject_bg1.getString("is_modifier"));
                        long l = item.updateItem("item_code=?", new String[]{item_code}, database);
                        if (l > 0) {
                            succ_bg = "1";
                        } else {
                        }

                        JSONArray json_item_location = jsonObject_bg1.getJSONArray("item_location");

                        for (int j = 0; j < json_item_location.length(); j++) {
                            JSONObject jsonObject_item_location = json_item_location.getJSONObject(j);
                            String loc_id = jsonObject_item_location.getString("location_id");
                            String itm_lc_id = jsonObject_item_location.getString("item_location_id");
                            item_location = Item_Location.getItem_Location(context, "WHERE item_code ='" + item_code + "'", database);
                            if (item_location == null) {
                                item_location = new Item_Location(context, null, jsonObject_item_location.getString("location_id"), jsonObject_item_location.getString("item_code"), jsonObject_item_location.getString("cost_price"), jsonObject_item_location.getString("markup"), jsonObject_item_location.getString("selling_price"), jsonObject_item_location.getString("quantity"), jsonObject_item_location.getString("loyalty_point"), jsonObject_item_location.getString("reorder_point"), jsonObject_item_location.getString("reorder_amount"), jsonObject_item_location.getString("is_inventory_tracking"), jsonObject_item_location.getString("is_active"), jsonObject_item_location.getString("modified_by"), jsonObject_item_location.getString("modified_date"), jsonObject_item_location.getString("new_sell_price"));
                                long itmlc = item_location.insertItem_Location(database);

                                if (itmlc > 0) {
                                    succ_bg = "1";

                                } else {
                                }
                            } else {
                                item_location = new Item_Location(context, item_location.get_item_location_id(), jsonObject_item_location.getString("location_id"), jsonObject_item_location.getString("item_code"), jsonObject_item_location.getString("cost_price"), jsonObject_item_location.getString("markup"), jsonObject_item_location.getString("selling_price"), jsonObject_item_location.getString("quantity"), jsonObject_item_location.getString("loyalty_point"), jsonObject_item_location.getString("reorder_point"), jsonObject_item_location.getString("reorder_amount"), jsonObject_item_location.getString("is_inventory_tracking"), jsonObject_item_location.getString("is_active"), jsonObject_item_location.getString("modified_by"), jsonObject_item_location.getString("modified_date"), jsonObject_item_location.getString("new_sell_price"));
                                long itmlc = item_location.updateItem_Location("item_code=? And item_location_id=? ", new String[]{item_code, item_location.get_item_location_id()}, database);

                                if (itmlc > 0) {
                                    succ_bg = "1";

                                } else {
                                }
                            }
                        }

                        JSONArray json_item_supplier = jsonObject_bg1.getJSONArray("item_supplier");
                        // item_supplier = Item_Supplier.getItem_Supplier(getApplicationContext(), "WHERE item_code ='" + item_code + "' and  contact_code = '"+ jsonObject_item_supplier.getString("contact_code").toString() +"'", database);
                        long l3 = Item_Supplier.delete_Item_Supplier(context, "item_code =?", new String[]{item_code}, database);

                        for (int k = 0; k < json_item_supplier.length(); k++) {
                            // Here wee will process loop according o json data For example : 2
                            JSONObject jsonObject_item_supplier = json_item_supplier.getJSONObject(k);
                            //getting item supplier infor from item code two records
                            item_supplier = new Item_Supplier(context, null, jsonObject_item_supplier.getString("item_code"), jsonObject_item_supplier.getString("contact_code"));
                            Log.i("qry", item_supplier.get_item_code() + "                 " + item_supplier.get_contact_code());
                            long itmsp = item_supplier.insertItem_Supplier(database);

                            if (itmsp > 0) {
                                succ_bg = "1";
                            } else {
                            }
                        }

                        long ll = Item_Group_Tax.delete_Item_Group_Tax(context, "item_group_code =?", new String[]{item_code}, database);
                        JSONArray json_item_tax = jsonObject_bg1.getJSONArray("item_tax");
                        for (int k = 0; k < json_item_tax.length(); k++) {
                            JSONObject jsonObject_item_tax = json_item_tax.getJSONObject(k);

                            item_group_tax = new Item_Group_Tax(context, jsonObject_item_tax.getString("location_id"), jsonObject_item_tax.getString("tax_id"), jsonObject_item_tax.getString("item_group_code"));
                            long itmsp = item_group_tax.insertItem_Group_Tax(database);

                            if (itmsp > 0) {
                                succ_bg = "1";
                            } else {
                            }
                        }
                    }
                }
            } else if (strStatus.equals("false")) {

                succ_bg = "3";
                Globals.responsemessage = strmsg;


            }
            if (succ_bg.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.endTransaction();
            }
        } catch (
                JSONException e
        ) {
            succ_bg = "2";
            database.endTransaction();
        }

        return succ_bg;
    }

   /* private String get_item_from_server(String datetime) {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                 Globals.App_IP_URL + "item");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("modified_date", datetime));
        nameValuePairs.add(new BasicNameValuePair("location_id", Globals.objLPD.getLocation_Code()));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.objLPD.getDevice_Code()));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", Globals.serialno));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", Globals.androidid));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", Globals.mykey));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", Globals.objLPR.getLicense_No()));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            serverData = EntityUtils.toString(httpEntity);
            Log.d("response", serverData);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;
    }
*/
    private String getTax(String serverData) {
      //  Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(context, database, db, "WHERE table_name='tax'");

        String succ_manu = "0";
        database.beginTransaction();

        try {
            final JSONObject jsonObject_tax = new JSONObject(serverData);
            final String strStatus = jsonObject_tax.getString("status");

            if (strStatus.equals("true")) {
                JSONArray jsonArray_tax = jsonObject_tax.getJSONArray("result");

                for (int i = 0; i < jsonArray_tax.length(); i++) {
                    JSONObject jsonObject_tax1 = jsonArray_tax.getJSONObject(i);
                    String taxId = jsonObject_tax1.getString("tax_id");
                    String taxlocation = jsonObject_tax1.getString("location_id");
                    Tax_Master tax_master = Tax_Master.getTax_Master(context, "WHERE tax_id ='" + taxId + "'", database, db);


                    if (tax_master == null) {
                        tax_master = new Tax_Master(context, jsonObject_tax1.getString("tax_id"), jsonObject_tax1.getString("location_id"), jsonObject_tax1.getString("tax_name"), jsonObject_tax1.getString("tax_type"), jsonObject_tax1.getString("rate"), jsonObject_tax1.getString("comment"), jsonObject_tax1.getString("is_active"), jsonObject_tax1.getString("modified_by"), jsonObject_tax1.getString("modified_date"), "Y");
                        long l = tax_master.insertTax_Master(database);
                        if (l > 0) {
                            succ_manu = "1";
                        } else {
                        }

                        JSONArray json_od_typ_tax = jsonObject_tax1.getJSONArray("order_type_tax");

                        for (int a2 = 0; a2 < json_od_typ_tax.length(); a2++) {
                            JSONObject jsonObject_od_typ_tax = json_od_typ_tax.getJSONObject(a2);
                            order_type_tax = new Order_Type_Tax(context, jsonObject_od_typ_tax.getString("location_id"), jsonObject_od_typ_tax.getString("tax_id"), jsonObject_od_typ_tax.getString("order_type_id"));
                            long odrtx = order_type_tax.insertOrder_Type_Tax(database);
                            if (odrtx > 0) {
                                succ_manu = "1";
                            } else {
                            }
                        }

                        JSONArray json_tax_detail = jsonObject_tax1.getJSONArray("tax_detail");

                        for (int a3 = 0; a3 < json_tax_detail.length(); a3++) {
                            JSONObject jsonObject_tax_detail = json_tax_detail.getJSONObject(a3);
                            tax_detail = new Tax_Detail(context, null, jsonObject_tax_detail.getString("tax_id"), jsonObject_tax_detail.getString("tax_type_id"));
                            long odrtx1 = tax_detail.insertTax_Detail(database);

                            if (odrtx1 > 0) {
                                succ_manu = "1";
                            } else {
                            }
                        }

                        JSONArray json_tax_group = jsonObject_tax1.getJSONArray("tax_group");

                        for (int a3 = 0; a3 < json_tax_group.length(); a3++) {
                            JSONObject jsonObject_tax_group = json_tax_group.getJSONObject(a3);
                            Sys_Tax_Group sys_tax_group = new Sys_Tax_Group(context, null,jsonObject_tax_group.getString("tax_master_id"),jsonObject_tax_group.getString("tax_id"));
                            long odrtx1 = sys_tax_group.insertSys_Tax_Group(database);

                            if (odrtx1 > 0) {
                                succ_manu = "1";
                            } else {
                            }
                        }
                    } else {
                        tax_master = new Tax_Master(context, taxId, jsonObject_tax1.getString("location_id"), jsonObject_tax1.getString("tax_name"), jsonObject_tax1.getString("tax_type"), jsonObject_tax1.getString("rate"), jsonObject_tax1.getString("comment"), jsonObject_tax1.getString("is_active"), jsonObject_tax1.getString("modified_by"), jsonObject_tax1.getString("modified_date"), "Y");
                        long l = tax_master.updateTax_Master("tax_id=? And location_id=?", new String[]{taxId, taxlocation}, database);
                        if (l > 0) {
                            succ_manu = "1";

                        } else {
                        }

                        JSONArray json_od_typ_tax = jsonObject_tax1.getJSONArray("order_type_tax");

                        for (int a2 = 0; a2 < json_od_typ_tax.length(); a2++) {
                            JSONObject jsonObject_od_typ_tax = json_od_typ_tax.getJSONObject(a2);
                            Order_Type_Tax order_type_tax5 = Order_Type_Tax.getOrder_Type_Tax(context,"WHERE tax_id = '"+taxId+"' and order_type_id='"+jsonObject_od_typ_tax.getString("order_type_id")+"'",database);
                            if (order_type_tax5==null){
                                order_type_tax = new Order_Type_Tax(context, jsonObject_od_typ_tax.getString("location_id"), jsonObject_od_typ_tax.getString("tax_id"), jsonObject_od_typ_tax.getString("order_type_id"));
                                long odrtx = order_type_tax.insertOrder_Type_Tax(database);
                                if (odrtx > 0) {
                                    succ_manu = "1";
                                } else {
                                }
                            }else {
                                order_type_tax = new Order_Type_Tax(context, jsonObject_od_typ_tax.getString("location_id"), jsonObject_od_typ_tax.getString("tax_id"), jsonObject_od_typ_tax.getString("order_type_id"));
                                long odrtx = order_type_tax.updateOrder_Type_Tax("tax_id=? And order_type_id=?", new String[]{taxId, jsonObject_od_typ_tax.getString("order_type_id")}, database);
                                if (odrtx > 0) {
                                    succ_manu = "1";
                                } else {
                                }
                            }

                        }

                        JSONArray json_tax_detail = jsonObject_tax1.getJSONArray("tax_detail");
                        for (int a3 = 0; a3 < json_tax_detail.length(); a3++) {
                            JSONObject jsonObject_tax_detail = json_tax_detail.getJSONObject(a3);
                            tax_detail = new Tax_Detail(context, jsonObject_tax_detail.getString("id"), jsonObject_tax_detail.getString("tax_id"), jsonObject_tax_detail.getString("tax_type_id"));
                            long odrtx1 = tax_detail.updateTax_Detail("tax_id=? And tax_type_id=?", new String[]{taxId, jsonObject_tax_detail.getString("tax_type_id")}, database);
                            if (odrtx1 > 0) {
                                succ_manu = "1";
                            } else {
                            }
                        }

                        JSONArray json_tax_group = jsonObject_tax1.getJSONArray("tax_group");

                        for (int a3 = 0; a3 < json_tax_group.length(); a3++) {
                            JSONObject jsonObject_tax_group = json_tax_group.getJSONObject(a3);
                            Sys_Tax_Group sys_tax_group = new Sys_Tax_Group(context, jsonObject_tax_group.getString("id"),jsonObject_tax_group.getString("tax_master_id") , jsonObject_tax_group.getString("tax_id"));
                            long odrtx1 = sys_tax_group.updateSys_Tax_Group("tax_id=? And tax_master_id=?", new String[]{taxId, jsonObject_tax_group.getString("tax_master_id")}, database);
                            if (odrtx1 > 0) {
                                succ_manu = "1";
                            } else {
                            }
                        }
                    }
                }
            } else {
                succ_manu = "0";
            }

            if (succ_manu.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.endTransaction();
            }

        } catch (JSONException e) {
            succ_manu = "2";
            database.endTransaction();
        }
        return succ_manu;
    }

  /*  private String get_tax_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(Globals.App_IP_URL + "tax");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            serverData = EntityUtils.toString(httpEntity);
            Log.d("response", serverData);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;
    }*/
    private String send_online_tax() {

        String result = Tax_Master.sendOnServer(context, database, db, "Select  * From tax  WHERE is_push = 'N'");
        return result;
    }
    private String send_online_unit() {

        String result = Unit.sendOnServer(context, database, db, "Select  * From unit  WHERE is_push = 'N'");
        return result;
    }


  /*  private String send_online() {
        String result = Pos_Balance.sendOnServer(context, database, db, "SELECT device_code,z_code,date,total_amount,is_active,modified_by  FROM  z_close Where is_push = 'N'", Globals.objLPR.getLicense_No());

        return result;
    }*/


    private String getPaymentMethod(String serverData) {
        String succ_manu = "0";
        // Call get bussiness group api here
        database.beginTransaction();

        try {
            final JSONObject jsonObject_bp = new JSONObject(serverData);
            final String strStatus = jsonObject_bp.getString("status");
            if (strStatus.equals("true")) {
                JSONArray jsonArray_bg = jsonObject_bp.getJSONArray("result");
                if (jsonArray_bg.length() > 0) {
                    long e6 = Payment.delete_Payment(context, "payments", "", new String[]{}, database);
                    for (int i = 0; i < jsonArray_bg.length(); i++) {

                        JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                        Payment payment = new Payment(context, jsonObject_bg1.getString("payment_id")
                                , jsonObject_bg1.getString("parent_id"),
                                jsonObject_bg1.getString("payment_name"),
                                jsonObject_bg1.getString("is_active"),
                                jsonObject_bg1.getString("modified_by"),
                                jsonObject_bg1.getString("modified_date"), "N");


                        long k = payment.insertPayment(database);
                        if (k > 0) {
                            succ_manu = "1";
                        }
                    }
                }
            }
            if (succ_manu.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.endTransaction();
            }
        } catch (
                JSONException e) {
            succ_manu = "3";
            database.endTransaction();
        }
        return succ_manu;
    }

  /*  private String get_payment_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                 Globals.App_IP_URL + "payment");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            serverData = EntityUtils.toString(httpEntity);
            Log.d("response", serverData);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;
    }*/

    public void getPayment_from_Server(final String registration_code) {

        /*pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.loggingin));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "payment";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result=getPaymentMethod(response);
                        } catch (Exception e) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                      //  pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("reg_code", registration_code);

                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void getbusiness_grpfrom_server() {

       /* pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.loggingin));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL +"business_group";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result=getBussinessGroup(response);
                        } catch (Exception e) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                      //  pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("company_id", Globals.objLPR.getcompany_id());

                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    public void getcontact_from_server(final String datetime,final String serial_no,final String android_id,final String myKey) {

      /*  pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "contact";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result=getContact(response);
                        } catch (Exception e) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(context,"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(context,"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(context,"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(context,"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                        pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code",Globals.objLPR.getRegistration_Code());
                params.put("modified_date", datetime);
                params.put("sys_code_1", serial_no);
                params.put("sys_code_2", Globals.syscode2);
                params.put("sys_code_3", android_id);
                params.put("sys_code_4", myKey);
                params.put("device_code", Globals.Device_Code);
                params.put("lic_customer_license_id",  Globals.objLPD.getLic_customer_license_id());


                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void getitemgrp_from_server(final ProgressDialog pDialog,final String datetime,final String serial_no,final String android_id,final String myKey) {

      /*  pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "item_group";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result=getitemGroup(response);

                            try {
                                //   result = send_online_unit();
                                Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(context, database, db, "WHERE table_name='unit'");

                                if (sys_sycntime == null) {
                                    get_unit_from_server("");
                                }
                                else {
                                    get_unit_from_server(sys_sycntime.get_datetime());
                                }
                            } catch (Exception e) {

                            }
                            try {
                                //  result = send_online_tax();
                                //Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(context, database, db, "WHERE table_name='tax'");


                                get_tax_from_server("");

                            } catch (Exception ex) {
                            }

                            try {
                                // result = send_online_item();
                                Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(context, database, db, "WHERE table_name='item'");

                                if (sys_sycntime == null) {
                                    get_item_from_server(pDialog,"",serial_no,android_id,myKey);
                                } else {
                                    get_item_from_server(pDialog,sys_sycntime.get_datetime(),serial_no,android_id,myKey);
                                }
                            } catch (Exception ex) {
                                System.out.println(ex.getMessage());
                            }


                            try {
                                // result = send_online_contact(serialno,android,mykey);
                                Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(context, database, db, "WHERE table_name='contact'");


                                //Call get contact api here
                                if (sys_sycntime == null) {
                                    getcontact_from_server("", serial_no, android_id, myKey);
                                } else {
                                    getcontact_from_server(sys_sycntime.get_datetime(), serial_no, android_id, myKey);
                                }


                            } catch (Exception ex) {
                            }




                            try {
                                //  send_online_user();
                                get_user_from_server();
                            } catch (Exception ex) {
                            }


                            try {
                                getPayment_from_Server(Globals.objLPR.getRegistration_Code());
                            } catch (Exception ex) {
                            }


                            try {
                                //  result = send_online_bussiness();
                                getbusiness_grpfrom_server();

                            } catch (Exception ex) {
                            }

                            try{
                                getScale_fromserver(serial_no,android_id,myKey, Globals.objLPR.getLicense_No());
                            }
                            catch(Exception e){

                            }
                            try{
                                getZone_Table_fromserver(serial_no, android_id, myKey);

                            }catch(Exception e){

                            }
                            try {
                                getInvoiceParameter_fromserver(pDialog,serial_no, android_id, myKey,  Globals.objLPD.getLic_customer_license_id());
                            } catch (Exception e) {

                            }
                        } catch (Exception e) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(context,"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(context,"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(context,"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(context,"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                        pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code",Globals.objLPR.getRegistration_Code());
                params.put("modified_date", datetime);
                params.put("sys_code_1", serial_no);
                params.put("sys_code_2", Globals.syscode2);
                params.put("sys_code_3", android_id);
                params.put("sys_code_4", myKey);
                params.put("device_code", Globals.Device_Code);
                params.put("lic_customer_license_id",  Globals.objLPD.getLic_customer_license_id());


                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }



    public void get_item_from_server(final ProgressDialog pDialog,final String datetime,final String serial_no,final String android_id,final String myKey) {

       /* pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncdataserver));
        pDialog.show();*/

        String server_url = Globals.App_IP_URL + "item";

        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result = getitem(response);

                            } catch(Exception e){
                            }


                       // pDialog.dismiss();

                           // ((MainActivity) ctx).finish();



                        /*    Intent i = new Intent(ctx, MainActivity.class);
                          i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            ctx.startActivity(i);*/
                            //Toast.makeText(ctx, ctx.getString(R.string.Data_sync_succ), Toast.LENGTH_SHORT).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(context,"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(context,"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(context,"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(context,"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                       // pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code",Globals.objLPR.getRegistration_Code());
                params.put("modified_date", datetime);
                params.put("location_id", Globals.objLPD.getLocation_Code());
                params.put("sys_code_1", serial_no);
                params.put("sys_code_2", Globals.syscode2);
                params.put("sys_code_3", android_id);
                params.put("sys_code_4", myKey);
                params.put("device_code", Globals.Device_Code);
                params.put("lic_customer_license_id",  Globals.objLPD.getLic_customer_license_id());


                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void get_tax_from_server(String datetime) {

      /*  pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "tax";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result = getTax(response);
                        } catch (Exception e) {
                        }


                    }
                },
        new Response.ErrorListener() {
@Override
public void onErrorResponse(VolleyError error) {
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
        Toast.makeText(context,"Network not available", Toast.LENGTH_SHORT).show();

        // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


        } else if (error instanceof AuthFailureError) {
        Toast.makeText(context,"Authentication issue", Toast.LENGTH_SHORT).show();
        //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

        } else if (error instanceof ServerError) {
        Toast.makeText(context,"Server not available", Toast.LENGTH_SHORT).show();

        //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

        } else if (error instanceof NetworkError) {
        Toast.makeText(context,"Network not available", Toast.LENGTH_SHORT).show();

        //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


        } else if (error instanceof ParseError) {

        }
        pDialog.dismiss();
        }
        }) {
@Override
protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();
        params.put("reg_code",Globals.objLPR.getRegistration_Code());
        System.out.println("params" + params);

        return params;
        }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
        }


public void get_user_from_server() {

      /*  pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncdataserver));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "user";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
        new Response.Listener<String>() {
@Override
public void onResponse(String response) {

        try {
        String result = getUser(response);
        } catch (Exception e) {
        }

            // pDialog.dismiss();
        }
        },
        new Response.ErrorListener() {
@Override
public void onErrorResponse(VolleyError error) {
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
        Toast.makeText(context,"Network not available", Toast.LENGTH_SHORT).show();

        // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


        } else if (error instanceof AuthFailureError) {
        Toast.makeText(context,"Authentication issue", Toast.LENGTH_SHORT).show();
        //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

        } else if (error instanceof ServerError) {
        Toast.makeText(context,"Server not available", Toast.LENGTH_SHORT).show();

        //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

        } else if (error instanceof NetworkError) {
        Toast.makeText(context,"Network not available", Toast.LENGTH_SHORT).show();

        //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


        } else if (error instanceof ParseError) {

        }
       // pDialog.dismiss();
        }
        }) {
@Override
protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();
        params.put("reg_code",Globals.objLPR.getRegistration_Code());
        System.out.println("params" + params);

        return params;
        }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
        }


    public void get_unit_from_server(String datetime) {

      /*  pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "unit";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result = getUnit(response);
                        } catch (Exception e) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(context,"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(context,"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(context,"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(context,"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                        pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code",Globals.objLPR.getRegistration_Code());
                params.put("modified_data","");
                System.out.println("params" + params);
                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    public void getInvoiceParameter_fromserver(final ProgressDialog pDialog,String serial_no,String android_id,String myKey,String liccustomerid) {

      /*  pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();*/
        String server_url = Globals.App_IP_URL + "invoice_parameter";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result = getinvoiceparameter(response);
                            if(result.equals("1")) {
                                 bflag=true;



                            }

                            if(bflag==true) {
                                if(Globals.objLPR.getIndustry_Type().equals("1")) {
                                    ((MainActivity) context).setReloadctivity(pDialog);
                                }
                                else if(Globals.objLPR.getIndustry_Type().equals("2")){
                                    ((Retail_IndustryActivity) context).setReloadctivity(pDialog);
                                }
                            }
                   /*         else if(result.equals("2")){
                                pDialog.dismiss();
                                Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(context, "", database);
                                lite_pos_device.setStatus("Out");
                                long ct = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                if (ct > 0) {
                                    database.endTransaction();

                                    Intent intent_category = new Intent(context, LoginActivity.class);
                                    context.startActivity(intent_category);
                                    ((MainActivity)context).finish();
                                }

                            }*/
                        } catch (Exception e) {
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(context,"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(context,"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(getApplicationContext(),  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(context,"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(getApplicationContext(),  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(context,"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(getApplicationContext(),  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }
                        pDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code", Globals.objLPR.getRegistration_Code());
                params.put("device_code", Globals.Device_Code);
                params.put("sys_code_1", serial_no);
                params.put("sys_code_2", Globals.syscode2);
                params.put("sys_code_3", android_id);
                params.put("sys_code_4", myKey);
                params.put("lic_customer_license_id", liccustomerid);
                System.out.println("params" + params);
                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }
    private String stock_post() {
        String suc = "0";
        returns = Returns.getReturns(context, " where voucher_no ='" + Globals.strvoucherno + "' ", database);
        returns.set_is_post("true");
        long l = returns.updateReturns("voucher_no=?", new String[]{Globals.strvoucherno}, database);
        if (l > 0) {
            suc = "1";
        }
        return suc;
    }



    @SuppressLint("NewApi")
    public String getPath(Context context, Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{split[1]};
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }



    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private String getLocationStock() {
        String succ_bg = "";
        database.beginTransaction();
        String serverData = "";
                //get_loc_stock_from_server();
        try {
            final JSONObject jsonObject_bg = new JSONObject(serverData);
            final String strStatus = jsonObject_bg.getString("status");
            if (strStatus.equals("true")) {
                JSONArray jsonArray_bg = jsonObject_bg.getJSONArray("result");
                for (int i = 0; i < jsonArray_bg.length(); i++) {
                    JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                    String item_code = jsonObject_bg1.getString("item_code");

                    item_location = Item_Location.getItem_Location(context, "WHERE item_code ='" + item_code + "'", database);
                    if (item_location == null) {
                    } else {
                        item_location.set_quantity(jsonObject_bg1.getString("quantity"));
                        long itmlc = item_location.updateItem_Location("item_code=?", new String[]{item_code}, database);
                        if (itmlc > 0) {
                            succ_bg = "1";
                        } else {
                        }
                    }
                }
            }

            if (succ_bg.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.endTransaction();
            }
        } catch (
                JSONException e
        ) {
            Globals.ErrorMsg = e.getMessage();
            succ_bg = "2";
            database.endTransaction();
        }
        return succ_bg;
    }

  /*  private String get_loc_stock_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
               Globals.App_IP_URL + "item/location");
        ArrayList nameValuePairs = new ArrayList(8);
        nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("location_id", Globals.objLPD.getLocation_Code()));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.objLPD.getDevice_Code()));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", Globals.serialno));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", Globals.androidid));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", Globals.mykey));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", Globals.objLPR.getLicense_No()));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e1) {
            //TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            serverData = EntityUtils.toString(httpEntity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;
    }*/

    private String getUnit(String serverData) {
        Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(context, database, db, "WHERE table_name='unit'");

        String succ_manu = "0";
        database.beginTransaction();

        try {

            final JSONObject jsonObject_unit = new JSONObject(serverData);
            final String strStatus = jsonObject_unit.getString("status");
            final String strResult = jsonObject_unit.getString("result");
            final JSONObject jsonObject = new JSONObject(strResult);
            if (strStatus.equals("true")) {

                JSONArray jsonArray_unit = jsonObject.getJSONArray("unit");
                for (int i = 0; i < jsonArray_unit.length(); i++) {
                    JSONObject jsonObjct_unit = jsonArray_unit.getJSONObject(i);
                    String unitId = jsonObjct_unit.getString("unit_id");

                    unit = Unit.getUnit(context,database,db,"where unit_id='"+unitId+"'");
                    if (sys_sycntime != null) {
                        sys_sycntime.set_datetime(jsonObjct_unit.getString("modified_date"));
                        long l1 = sys_sycntime.updateSys_Sycntime("table_name=?", new String[]{"unit"}, database);
                    }
                    if (unit == null) {
                        unit = new Unit(context, null,jsonObjct_unit.getString("name"), jsonObjct_unit.getString("code"), jsonObjct_unit.getString("description"), jsonObjct_unit.getString("is_active"), jsonObjct_unit.getString("modified_by"), jsonObjct_unit.getString("modified_date"), "Y");
                        long l = unit.insertUnit(database);
                        if (l > 0) {
                            succ_manu = "1";

                        } else {
                        }

                    } else {
                        unit = new Unit(context, unitId,jsonObjct_unit.getString("name"), jsonObjct_unit.getString("code"), jsonObjct_unit.getString("description"), jsonObjct_unit.getString("is_active"), jsonObjct_unit.getString("modified_by"), jsonObjct_unit.getString("modified_date"), "Y");
                        long l = unit.updateUnit("unit_id=?", new String[]{unitId}, database);
                        if (l > 0) {
                            succ_manu = "1";

                        } else {
                        }
                    }
                }
            } else {
                succ_manu = "0";
            }

            if (succ_manu.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.endTransaction();
            }

        } catch (JSONException e) {
            succ_manu = "2";
            database.endTransaction();
        }
        return succ_manu;
    }

   /* private String get_unit_from_server() {

        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                 Globals.App_IP_URL + "unit");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code",Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("modified_data",""));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            serverData = EntityUtils.toString(httpEntity);
            Log.d("response", serverData);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;

    }
*/
    private void send_online_user() {
        User.sendOnServer(context, database, db, "Select  * From  user  WHERE is_push = 'N'");
    }
    private String getUser(String serverData) {
        String succ_manu = "0";
        // Call get bussiness group api here

        database.beginTransaction();
        try {
            final JSONObject jsonObject_bp = new JSONObject(serverData);
            final String strStatus = jsonObject_bp.getString("status");
            if (strStatus.equals("true")) {

                JSONArray jsonArray_bg = jsonObject_bp.getJSONArray("result");
                for (int i = 0; i < jsonArray_bg.length(); i++) {
                    JSONObject jsonObject_bg1 = jsonArray_bg.getJSONObject(i);
                    String bg_code = jsonObject_bg1.getString("user_code");
                    String bg_id = jsonObject_bg1.getString("user_id");
                    user = User.getUser(context, "WHERE  is_active = '1' and user_code ='" + bg_code + "'", database);

                    if (user == null) {
                        user = new User(context, null, jsonObject_bg1.getString("user_group_id"), jsonObject_bg1.getString("user_code"), jsonObject_bg1.getString("name"), jsonObject_bg1.getString("email"), jsonObject_bg1.getString("password"), jsonObject_bg1.getString("max_discount"), jsonObject_bg1.getString("image"), jsonObject_bg1.getString("is_active"), "0", "0", "N", jsonObject_bg1.getString("app_user_permission"));
                        long l = user.insertUser(database);

                        if (l > 0) {
                            succ_manu = "1";


                        } else {

                        }

                    } else {

                        user = new User(context, bg_id, jsonObject_bg1.getString("user_group_id"), jsonObject_bg1.getString("user_code"), jsonObject_bg1.getString("name"), jsonObject_bg1.getString("email"), jsonObject_bg1.getString("password"), jsonObject_bg1.getString("max_discount"), jsonObject_bg1.getString("image"), jsonObject_bg1.getString("is_active"), "0", "0", "N", jsonObject_bg1.getString("app_user_permission"));
                        long l = user.updateUser("user_code=? And user_id=?", database, new String[]{bg_code, bg_id});
                        if (l > 0) {
                            succ_manu = "2";


                        } else {

                        }
                    }
                }
            } else {
                database.endTransaction();
            }
            if (succ_manu.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else if (succ_manu.equals("2")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            }
            else{
                database.endTransaction();
            }
        } catch (JSONException e) {
            succ_manu = "3";
            database.endTransaction();
        }
        return succ_manu;
    }


    //  getScale Api

    public void getScale_fromserver(String serial_no,String android_id,String myKey,String liccustomerid) {

      /*  pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();*/

        String server_url = Globals.App_IP_URL + "scale_setup";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String result = getScalesetup(response);

                            if(result.equals("1")){


                                //  Toast.makeText(context,R.string.Data_sync_succ,Toast.LENGTH_LONG).show();


                            }
                            else if(result.equals("2")){
                                Toast.makeText(context,R.string.Data_sync_succ,Toast.LENGTH_LONG).show();

                                // finish();
                            }

                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(context,"Network not available", Toast.LENGTH_SHORT).show();

                            // Globals.showToast(context,  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(context,"Authentication issue", Toast.LENGTH_SHORT).show();
                            //  Globals.showToast(context,  "Authentication issue", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof ServerError) {
                            Toast.makeText(context,"Server not available", Toast.LENGTH_SHORT).show();

                            //Globals.showToast(context,  "Server not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);

                        } else if (error instanceof NetworkError) {
                            Toast.makeText(context,"Network not available", Toast.LENGTH_SHORT).show();

                            //  Globals.showToast(context,  "Network not available", Globals.txtSize, "#ffffff", "#e51f13", "short", Globals.gravity, 0, 0);


                        } else if (error instanceof ParseError) {

                        }

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_code", Globals.objLPR.getRegistration_Code());
                params.put("device_code", Globals.Device_Code);
                params.put("sys_code_1", serial_no);
                params.put("sys_code_2", Globals.syscode2);
                params.put("sys_code_3", android_id);
                params.put("sys_code_4", myKey);
                params.put("lic_customer_license_id", liccustomerid);
                System.out.println("params" + params);
                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    private String getScalesetup(String serverData) {
        String succ_manu = "0";
        // Call get bussiness group api here

        database.beginTransaction();
        try {
            final JSONObject jsonObject_bp = new JSONObject(serverData);
            final String strStatus = jsonObject_bp.getString("status");
            if (strStatus.equals("true")) {

                JSONArray jsonArray = jsonObject_bp.getJSONArray("result");
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject_res= jsonArray.getJSONObject(i);
                    String scaleid= jsonObject_res.getString("scale_id");
                    String scalename= jsonObject_res.getString("scale_name");
                    String pluvalue= jsonObject_res.getString("plu_value");
                    String plulength= jsonObject_res.getString("plu_length");
                    String itemcodelength= jsonObject_res.getString("item_code_length");
                    String wplen= jsonObject_res.getString("wp_length");
                    String wpcal= jsonObject_res.getString("wp_cal");
                    String isweight= jsonObject_res.getString("is_weight");
                    String pcsunitid= jsonObject_res.getString("pcs_unit_id");
                    String plubarcodelen= jsonObject_res.getString("plu_barcode_length");
                    String locationid= jsonObject_res.getString("location_id");

                    ScaleSetup scaleSetup = new ScaleSetup(context, null, scalename,
                            pluvalue, plulength, itemcodelength, wplen, wpcal, isweight,pcsunitid,plubarcodelen);


                    long k = scaleSetup.insertScaleSetup(database);
                    if (k > 0) {
                        succ_manu = "1";
                    }


                }


            } else {
                database.endTransaction();
            }
            if (succ_manu.equals("1")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else if (succ_manu.equals("2")) {
                database.setTransactionSuccessful();
                database.endTransaction();
            } else {
                database.endTransaction();
            }
        } catch (JSONException e) {
            succ_manu = "3";
            database.endTransaction();
        }
        return succ_manu;
    }

    /*private String get_user_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                 Globals.App_IP_URL + "user");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code",Globals.objLPR.getRegistration_Code()));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            serverData = EntityUtils.toString(httpEntity);
            Log.d("response", serverData);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;

    }*/
}