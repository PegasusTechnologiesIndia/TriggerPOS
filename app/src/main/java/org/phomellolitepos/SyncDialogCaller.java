package org.phomellolitepos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import org.phomellolitepos.database.Pos_Balance;
import org.phomellolitepos.database.Returns;
import org.phomellolitepos.database.Sys_Sycntime;
import org.phomellolitepos.database.Sys_Tax_Group;
import org.phomellolitepos.database.Tax_Detail;
import org.phomellolitepos.database.Tax_Master;
import org.phomellolitepos.database.Unit;
import org.phomellolitepos.database.User;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class SyncDialogCaller{
    ProgressDialog progressDialog;
    Context context;
    SQLiteDatabase database;
    Item_Location item_location;
    Item_Group item_group;
    Item_Group_Tax item_group_tax;

    Tax_Master tax_master;
    Order_Type_Tax order_type_tax;
    Tax_Detail tax_detail;
    Contact contact;
    Address address;
    Unit unit;
    Address_Lookup address_lookup;
    Contact_Bussiness_Group contact_bussiness_group;
    Database db;
    User user;
    Returns returns;
    Item item;
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

    public void sync_all(final Context ctx, SQLiteDatabase database,String serialno,String
                          android, String mykey,String liccustid){


                        try {

                            String result;
                            String suss;



                            try {
                                result = getPaymentMethod();
                            } catch (Exception ex) {
                            }



                            try {
                              //  result = send_online_bussiness();
                                suss = getBussinessGroup();
                            } catch (Exception ex) {
                            }

                            try {
                               // result = send_online_contact(serialno,android,mykey);
                                suss = getContact(serialno,android,mykey);
                            } catch (Exception ex) {
                            }

                            try {
                               // result = send_online_item_group();
                                suss = getitemGroup(serialno,android,mykey,liccustid);
                            } catch (Exception ex) {
                            }

                            try {
                               // result = send_online_item();
                                suss = getitem();
                            } catch (Exception ex) {
                            }


                            // call Item Location Stock Same Logic Copy Here
                          /*  try {

                                result = getLocationStock();
                            } catch (Exception e) {

                            }*/

                            try {
                              //  result = send_online_tax();
                                suss = getTax();
                            } catch (Exception ex) {
                            }

                            try {
                              //  send_online_user();
                                suss = getUser();
                            } catch (Exception ex) {
                            }



                            try{
                             //   result = send_online_unit();
                                suss = getUnit();
                            }
                            catch(Exception e){

                            }
                            try {
                                getinvoiceparameter(serialno,android,mykey,liccustid);
                            }
                            catch(Exception e){

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


    private String getBussinessGroup() {
        String succ_manu = "0";
        // Call get bussiness group api here
        Bussiness_Group bussiness_group;
        database.beginTransaction();
        String serverData = get_bussiness_gp_from_server();
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

    private String get_bussiness_gp_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos/index.php/api/business_group");
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

    }

    private String send_online_bussiness() {
        String l = Bussiness_Group.sendOnServer(context, database, db, "Select  * From business_group  WHERE is_push = 'N'");
        return l;
    }

    private String send_online_item_group() {
        //Globals.reg_code = lite_pos_registration.getRegistration_Code();
        String l = Item_Group.sendOnServer(context, database, db, "Select * From item_group  WHERE is_push = 'N'", Globals.serialno, Globals.syscode2, Globals.androidid, Globals.mykey, Globals.objLPR.getLicense_No());
        return l;
    }

    private String getitemGroup(String seial_no,String android_id,String my_key,String liccustomerid) {
        String serverData;
        Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(context, database, db, "WHERE table_name ='item_group'");
        String succ_bg = "0";

        // Call get item group api here
        System.out.println("get sync date" + sys_sycntime.get_datetime());
        database.beginTransaction();
        if (sys_sycntime == null) {
            serverData = get_item_gp_from_server("",seial_no,android_id,my_key,liccustomerid);
        } else {
            serverData = get_item_gp_from_server(sys_sycntime.get_datetime(),seial_no,android_id,my_key,liccustomerid);
        }

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
                        item_group = new Item_Group(context, null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("parent_code"), jsonObject_bg1.getString("item_group_name"), "0", jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y");
                        long l = item_group.insertItem_Group(database);
                        if (l > 0) {
                            succ_bg = "1";
                        } else {
                        }
                    } else {
                        item_group = new Item_Group(context, item_group.get_item_group_id(), jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("parent_code"), jsonObject_bg1.getString("item_group_name"), "0", jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y");
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
    private String getinvoiceparameter(String serial_no,String androidid,String mykey,String licustid) {
        String succ_manu = "0";
        // Call get bussiness group api here
      Lite_POS_Registration  lite_pos_registration = Lite_POS_Registration.getRegistration(context, database, db, "");
        String serverData = getInvoiceParameter_fromserver(serial_no,androidid,mykey,licustid);
        database.beginTransaction();

        try {
            final JSONObject jsonObject_bp = new JSONObject(serverData);
            final String strStatus = jsonObject_bp.getString("status");
            if (strStatus.equals("true")) {

                JSONObject jsonobj_bg = jsonObject_bp.getJSONObject("result");
                String gst_number=jsonobj_bg.getString("gst_number");
                String inv_address=jsonobj_bg.getString("invoice_address");
                lite_pos_registration.setAddress(inv_address);
                lite_pos_registration.setService_code_tariff(gst_number);

                long ct = lite_pos_registration.updateRegistration("Id=?", new String[]{"1"}, database);
                if(ct>0){
                    succ_manu="1";
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


    private String getInvoiceParameter_fromserver(String serial_no,String android_id,String myKey,String liccustomerid) {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/invoice_parameter");
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
    }
    private String get_item_gp_from_server(String datetime,String serial_no,String android_id,String myKey,String liccustomerid) {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/item_group");
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

    private String send_online_contact(String serial_no,String android_id,String myKey) {

        String conList = Contact.sendOnServer(context, database, db, "Select device_code, contact_code,title,name,gender,dob,company_name,description,contact_1,contact_2,email_1,email_2,is_active,modified_by,credit_limit,gstin,country_id,zone_id from contact where is_push='N'", Globals.objLPR.getLicense_No(), serial_no, android_id, myKey);
        return conList;
    }

    private String getContact(String serialno,String androidid,String mykey) {
        String serverData;
        Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(context, database, db, "WHERE table_name='contact'");

        String succ_bg = "0";
        database.beginTransaction();
        //Call get contact api here
        if (sys_sycntime==null){
            serverData = get_contact_from_server("",serialno,androidid,mykey);
        }else {
            serverData = get_contact_from_server(sys_sycntime.get_datetime(),serialno,androidid,mykey);
        }

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
                        contact = new Contact(context, null, jsonObject_contact1.getString("device_code"), jsonObject_contact1.getString("contact_code"), jsonObject_contact1.getString("title"), jsonObject_contact1.getString("name"), jsonObject_contact1.getString("gender"), jsonObject_contact1.getString("dob"), jsonObject_contact1.getString("company_name"), jsonObject_contact1.getString("description"), jsonObject_contact1.getString("contact_1"), jsonObject_contact1.getString("contact_2"), jsonObject_contact1.getString("email_1"), jsonObject_contact1.getString("email_2"), jsonObject_contact1.getString("is_active"), jsonObject_contact1.getString("modified_by"), "Y", "0", jsonObject_contact1.getString("modified_date"), "0", jsonObject_contact1.getString("gstin"), jsonObject_contact1.getString("country_id"), jsonObject_contact1.getString("zone_id"));
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
                        contact = new Contact(context, contact.get_contact_id(), jsonObject_contact1.getString("device_code"), jsonObject_contact1.getString("contact_code"), jsonObject_contact1.getString("title"), jsonObject_contact1.getString("name"), jsonObject_contact1.getString("gender"), jsonObject_contact1.getString("dob"), jsonObject_contact1.getString("company_name"), jsonObject_contact1.getString("description"), jsonObject_contact1.getString("contact_1"), jsonObject_contact1.getString("contact_2"), jsonObject_contact1.getString("email_1"), jsonObject_contact1.getString("email_2"), jsonObject_contact1.getString("is_active"), jsonObject_contact1.getString("modified_by"), "Y", "0", jsonObject_contact1.getString("modified_date"), "0", jsonObject_contact1.getString("gstin"), jsonObject_contact1.getString("country_id"), jsonObject_contact1.getString("zone_id"));

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

    private String get_contact_from_server(String datetime,String serial_no,String android_id,String myKey) {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/contact");
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
    }

    private String send_online_item() {
        String result = Item.sendOnServer(context, database, db, "Select device_code, item_code,parent_code,item_group_code,manufacture_code,item_name,description,sku,barcode,image,hsn_sac_code,item_type,unit_id,is_return_stockable,is_service,is_active,modified_by,is_inclusive_tax FROM item  WHERE is_push = 'N'", Globals.objLPR.getLicense_No());
        return result;
    }

    private String getitem() {
        String serverData;
        Sys_Sycntime sys_sycntime = Sys_Sycntime.getSys_Sycntime(context, database, db, "WHERE table_name='item'");
        String succ_bg = "0";
        // Call get item api here
        database.beginTransaction();
        if (sys_sycntime == null) {
            serverData = get_item_from_server("");
        } else {
            serverData = get_item_from_server(sys_sycntime.get_datetime());
        }

        try {
            final JSONObject jsonObject_bg = new JSONObject(serverData);
            final String strStatus = jsonObject_bg.getString("status");
            final String strmsg = jsonObject_bg.getString("message");
            if (strStatus.equals("true")) {
                JSONArray jsonArray_bg = jsonObject_bg.getJSONArray("result");
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

                        item = new Item(context, null, jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_code"), "1", jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("manufacture_code"), jsonObject_bg1.getString("item_name"), jsonObject_bg1.getString("description"), jsonObject_bg1.getString("sku"), jsonObject_bg1.getString("barcode"), jsonObject_bg1.getString("hsn_sac_code"), path, jsonObject_bg1.getString("item_type"), jsonObject_bg1.getString("unit_id"), jsonObject_bg1.getString("is_return_stockable"), jsonObject_bg1.getString("is_service"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y", jsonObject_bg1.getString("is_inclusive_tax"), path);
                        long l = item.insertItem(database);

                        if (l > 0) {
                            succ_bg = "1";
                        } else {
                        }

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

                        for (int k = 0; k < json_item_tax.length(); k++) {
                            JSONObject jsonObject_item_tax = json_item_tax.getJSONObject(k);
                            item_group_tax = new Item_Group_Tax(context, jsonObject_item_tax.getString("location_id"), jsonObject_item_tax.getString("tax_id"), jsonObject_item_tax.getString("item_group_code"));
                            long itmsp = item_group_tax.insertItem_Group_Tax(database);

                            if (itmsp > 0) {
                                succ_bg = "1";
                            } else {
                            }
                        }

                    } else {
                        item = new Item(context, item.get_item_id(), jsonObject_bg1.getString("device_code"), jsonObject_bg1.getString("item_code"), "1", jsonObject_bg1.getString("item_group_code"), jsonObject_bg1.getString("manufacture_code"), jsonObject_bg1.getString("item_name"), jsonObject_bg1.getString("description"), jsonObject_bg1.getString("sku"), jsonObject_bg1.getString("barcode"), jsonObject_bg1.getString("hsn_sac_code"), path, jsonObject_bg1.getString("item_type"), jsonObject_bg1.getString("unit_id"), jsonObject_bg1.getString("is_return_stockable"), jsonObject_bg1.getString("is_service"), jsonObject_bg1.getString("is_active"), jsonObject_bg1.getString("modified_by"), jsonObject_bg1.getString("modified_date"), "Y", jsonObject_bg1.getString("is_inclusive_tax"), path);
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

    private String get_item_from_server(String datetime) {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/item");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("modified_data", datetime));
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

    private String getTax() {
        String succ_manu = "0";
        database.beginTransaction();
        String serverData = get_tax_from_server();
        try {
            final JSONObject jsonObject_tax = new JSONObject(serverData);
            final String strStatus = jsonObject_tax.getString("status");

            if (strStatus.equals("true")) {
                JSONArray jsonArray_tax = jsonObject_tax.getJSONArray("result");
                for (int i = 0; i < jsonArray_tax.length(); i++) {
                    JSONObject jsonObject_tax1 = jsonArray_tax.getJSONObject(i);
                    String taxId = jsonObject_tax1.getString("tax_id");
                    String taxlocation = jsonObject_tax1.getString("location_id");
                    tax_master = Tax_Master.getTax_Master(context, "WHERE tax_id ='" + taxId + "'", database, db);
                    if (tax_master == null) {
                        tax_master = new Tax_Master(context, null, jsonObject_tax1.getString("location_id"), jsonObject_tax1.getString("tax_name"), jsonObject_tax1.getString("tax_type"), jsonObject_tax1.getString("rate"), jsonObject_tax1.getString("comment"), jsonObject_tax1.getString("is_active"), jsonObject_tax1.getString("modified_by"), jsonObject_tax1.getString("modified_date"), "Y");
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

                        for (int a3 = 0; a3 < json_tax_detail.length(); a3++) {
                            JSONObject jsonObject_tax_group = json_tax_group.getJSONObject(a3);
                            Sys_Tax_Group sys_tax_group = new Sys_Tax_Group(context, null, jsonObject_tax_group.getString("tax_id"), jsonObject_tax_group.getString("tax_master_id"));
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

                        for (int a3 = 0; a3 < json_tax_detail.length(); a3++) {
                            JSONObject jsonObject_tax_group = json_tax_group.getJSONObject(a3);
                            Sys_Tax_Group sys_tax_group = new Sys_Tax_Group(context, jsonObject_tax_group.getString("id"), jsonObject_tax_group.getString("tax_id"), jsonObject_tax_group.getString("tax_master_id"));
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

    private String get_tax_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/tax");
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
    }
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


    private String getPaymentMethod() {
        String succ_manu = "0";
        // Call get bussiness group api here
        database.beginTransaction();
        String serverData = get_payment_from_server();
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

    private String get_payment_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/payment");
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
        String serverData = get_loc_stock_from_server();
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

    private String get_loc_stock_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/item/location");
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
    }

    private String getUnit() {

        String succ_manu = "0";
        database.beginTransaction();
        String serverData = get_unit_from_server();
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

    private String get_unit_from_server() {

        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/unit");
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

    private void send_online_user() {
        User.sendOnServer(context, database, db, "Select  * From  user  WHERE is_push = 'N'");
    }
    private String getUser() {
        String succ_manu = "0";
        // Call get bussiness group api here
        String serverData = get_user_from_server();
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

    private String get_user_from_server() {
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/user");
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

    }
}