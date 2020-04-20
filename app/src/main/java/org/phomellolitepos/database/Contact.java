package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.phomellolitepos.Util.Globals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


/**
 * Created by Neeraj on 3/15/2017.
 */

public class Contact {

    private static String tableName = "contact";
    private String contact_id;
    private String device_code;
    private String contact_code;
    private String title;
    private String name;
    private String gender;
    private String dob;
    private String company_name;
    private String description;
    private String contact_1;
    private String contact_2;
    private String email_1;
    private String email_2;
    private String is_active;
    private String modified_by;
    private String is_push;
    private String address;
    private String modified_date;
    private String credit_limit;
    private String gstin;
    private String country_id;
    private String zone_id;


    private Database db;
    private ContentValues value;


    public Contact(Context context, String contact_id, String device_code,
                   String contact_code, String title, String name, String gender, String dob, String company_name, String description, String contact_1, String contact_2, String email_1, String email_2, String is_active, String modified_by, String is_push,String address,String modified_date,String credit_limit,String gstin,String country_id,String zone_id) {

        db = new Database(context);
        value = new ContentValues();

        this.set_contact_id(contact_id);
        this.set_device_code(device_code);
        this.set_contact_code(contact_code);
        this.set_title(title);
        this.set_name(name);
        this.set_gender(gender);
        this.set_dob(dob);
        this.set_company_name(company_name);
        this.set_description(description);
        this.set_contact_1(contact_1);
        this.set_contact_2(contact_2);
        this.set_email_1(email_1);
        this.set_email_2(email_2);
        this.set_is_active(is_active);
        this.set_modified_by(modified_by);
        this.set_is_push(is_push);
        this.set_address(address);
        this.set_modified_date(modified_date);
        this.set_credit_limit(credit_limit);
        this.set_gstin(gstin);
        this.set_country_id(country_id);
        this.set_zone_id(zone_id);

    }


    public String get_contact_id() {
        return contact_id;
    }

    public void set_contact_id(String contact_id) {
        this.contact_id = contact_id;
        value.put("contact_id", contact_id);
    }

    public String get_device_code() {
        return device_code;
    }

    public void set_device_code(String device_code) {
        this.device_code = device_code;
        value.put("device_code", device_code);
    }

    public String get_contact_code() {
        return contact_code;
    }

    public void set_contact_code(String contact_code) {
        this.contact_code = contact_code;
        value.put("contact_code", contact_code);
    }

    public String get_title() {
        return title;
    }

    public void set_title(String title) {
        this.title = title;
        value.put("title", title);
    }

    public String get_name() {
        return name;
    }

    public void set_name(String name) {
        this.name = name;
        value.put("name", name);
    }

    public String get_gender() {
        return gender;
    }

    public void set_gender(String gender) {
        this.gender = gender;
        value.put("gender", gender);
    }

    public String get_dob() {
        return dob;
    }

    public void set_dob(String dob) {
        this.dob = dob;
        value.put("dob", dob);
    }

    public String get_company_name() {
        return company_name;
    }

    public void set_company_name(String company_name) {
        this.company_name = company_name;
        value.put("company_name", company_name);
    }


    public String get_description() {
        return description;
    }

    public void set_description(String description) {
        this.description = description;
        value.put("description", description);
    }

    public String get_contact_1() {
        return contact_1;
    }

    public void set_contact_1(String contact_1) {
        this.contact_1 = contact_1;
        value.put("contact_1", contact_1);
    }


    public String get_contact_2() {
        return contact_2;
    }

    public void set_contact_2(String contact_2) {
        this.contact_2 = contact_2;
        value.put("contact_2", contact_2);
    }

    public String get_email_1() {
        return email_1;
    }

    public void set_email_1(String email_1) {
        this.email_1 = email_1;
        value.put("email_1", email_1);
    }


    public String get_email_2() {
        return email_2;
    }

    public void set_email_2(String email_2) {
        this.email_2 = email_2;
        value.put("email_2", email_2);
    }

    public String get_is_active() {
        return is_active;
    }

    public void set_is_active(String is_active) {
        this.is_active = is_active;
        value.put("is_active", is_active);
    }


    public String get_modified_by() {
        return modified_by;
    }

    public void set_modified_by(String modified_by) {
        this.modified_by = modified_by;
        value.put("modified_by", modified_by);
    }


    public String get_is_push() {
        return is_push;
    }

    public void set_is_push(String is_push) {
        this.is_push = is_push;
        value.put("is_push", is_push);
    }


    public String get_address() {
        return address;
    }

    public void set_address(String address) {
        this.address = address;
        value.put("address", address);
    }


    public String get_modified_date() {
        return modified_date;
    }

    public void set_modified_date(String modified_date) {
        this.modified_date = modified_date;
        value.put("modified_date", modified_date);
    }

    public String get_credit_limit() {
        return credit_limit;
    }

    public void set_credit_limit(String credit_limit) {
        this.credit_limit = credit_limit;
        value.put("credit_limit", credit_limit);
    }


    public String get_gstin() {
        return gstin;
    }

    public void set_gstin(String gstin) {
        this.gstin = gstin;
        value.put("gstin", gstin);
    }

    public String get_country_id() {
        return country_id;
    }

    public void set_country_id(String country_id) {
        this.country_id = country_id;
        value.put("country_id", country_id);
    }

    public String get_zone_id() {
        return zone_id;
    }

    public void set_zone_id(String zone_id) {
        this.zone_id = zone_id;
        value.put("zone_id", zone_id);
    }

    public long insertContact(SQLiteDatabase database) {
        //SQLiteDatabase database = db.getWritableDatabase();
        long insert=0;
        try {
            insert = database.insert(tableName, "contact_id", value);
        }catch (Exception ex){
            String ab = ex.getMessage();
            ab=ab;
        }

        //database.close();
        return insert;
    }

//    public static long delete_Contact(Context context, String whereClause, String[] whereArgs) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
//        sdb.delete(tableName, whereClause, whereArgs);
//
//        sdb.close();
//        return 1;
//    }

    public long updateContact(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static Contact getContact(Context context, SQLiteDatabase database, Database db, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Contact master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Contact(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6), cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getString(13),
                        cursor.getString(14),
                        cursor.getString(15),
                        cursor.getString(16),
                        cursor.getString(17),
                        cursor.getString(18),
                        cursor.getString(19),
                        cursor.getString(20),
                        cursor.getString(21));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Contact> getAllContact(Context context, SQLiteDatabase database, Database db, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Contact> list = new ArrayList<Contact>();
        Contact master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Contact(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getString(13),
                        cursor.getString(14),
                        cursor.getString(15),
                        cursor.getString(16),
                        cursor.getString(17),
                        cursor.getString(18),
                        cursor.getString(19),
                        cursor.getString(20),
                        cursor.getString(21));
                    list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }


    // Here Changed in function  need to update all classes
    public static ArrayList<String> getAllContactName(Context context, SQLiteDatabase database, Database db, String WhereClasue) {
        String Query = "Select contact.name  FROM " + tableName + " inner join contact_business_group on contact.contact_code=contact_business_group.contact_code " + WhereClasue;
        ArrayList<String> list = new ArrayList<String>();
        //Contact master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {



                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }




    public static String  sendOnServer(Context context, SQLiteDatabase database, Database db, String strTableQry,String liccustomerid,String syscode1,String syscode3,String syscode4) {
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        String strContactCode = "",conStr="0";
        Cursor cursor = database.rawQuery(strTableQry, null);
        try {
            int columnCount = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                JSONObject sender = new JSONObject();
                JSONArray result = new JSONArray();
                JSONObject row = new JSONObject();
                JSONArray array_address = new JSONArray();
                JSONObject address_row = new JSONObject();
                JSONArray array_item_location_tax = new JSONArray();
                JSONArray array_add_lookup = new JSONArray();
                JSONObject add_lookup = new JSONObject();
                JSONArray array_con_bg = new JSONArray();
                JSONObject con_bg = new JSONObject();

                strContactCode = cursor.getString(1);
                for (int index = 0; index < columnCount; index++) {
                    row.put(cursor.getColumnName(index).toLowerCase(), cursor.getString(index));
                }
                String address_qry = "Select device_code, address_code,address_category_code,area_id,address,landmark,latitude,longitude,contact_person,contact,is_active,modified_by from address where address_code  = '" + strContactCode + "'";
                Cursor cursor_address = database.rawQuery(address_qry, null);
                try {
                    int columnCount_address = cursor_address.getColumnCount();
                    while (cursor_address.moveToNext()) {
                        //item_location_row.put("jjj", cursor_location.getString(index));
                        for (int index = 0; index < columnCount_address; index++) {
                            address_row.put(cursor_address.getColumnName(index).toLowerCase(), cursor_address.getString(index));
                        }
                        array_address.put(address_row);
                    }
                    cursor_address.close();

                String add_lookup_qry = "Select device_code, address_code,refrence_type,refrence_code from address_lookup where refrence_code  = '" + strContactCode + "'";
                Cursor cursor_add_lookup = database.rawQuery(add_lookup_qry, null);
                try {
                    int columnCount_add_lookup = cursor_add_lookup.getColumnCount();
                    while (cursor_add_lookup.moveToNext()) {

                        //item_location_row.put("jjj", cursor_location.getString(index));
                        for (int index = 0; index < columnCount_add_lookup; index++) {
                            add_lookup.put(cursor_add_lookup.getColumnName(index).toLowerCase(), cursor_add_lookup.getString(index));
                        }
                        array_add_lookup.put(add_lookup);
                    }

                } catch (Exception ex) {
                }

                    String add_con_bg_qry = "Select contact_code, business_group_code from contact_business_group where contact_code = '" + strContactCode + "'";
                    Cursor cursor_con_bg = database.rawQuery(add_con_bg_qry, null);
                    try {
                        int columnCount_con_bg = cursor_con_bg.getColumnCount();
                        while (cursor_con_bg.moveToNext()) {
                            con_bg = new JSONObject();
                            //item_location_row.put("jjj", cursor_location.getString(index));
                            for (int index = 0; index < columnCount_con_bg; index++) {
                                con_bg.put(cursor_con_bg.getColumnName(index).toLowerCase(), cursor_con_bg.getString(index));

                            }
                            array_con_bg.put(con_bg);
                        }


                    } catch (Exception ex) {
                    }

                } catch (Exception ex) {
                }
                row.put("address", array_address);
                row.put("address_lookup", array_add_lookup);
                row.put("contact_business_group", array_con_bg);
                result.put(row);
                sender.put("contact".toLowerCase(), result);
                String serverData = send_item_json_on_server(sender.toString(),liccustomerid,syscode1,syscode3,syscode4);
                final JSONObject collection_jsonObject1 = new JSONObject(serverData);
                final String strStatus = collection_jsonObject1.getString("status");
                final String strmsg = collection_jsonObject1.getString("message");
                if (strStatus.equals("true")) {
                    // Update This Item Group Push True
                    database.beginTransaction();
                    String Query = "Update  contact Set is_push = 'Y' Where contact_code = '" + strContactCode + "'";
                    long check = db.executeDML(Query,database);
                    if (check>0){
                        conStr="1";
                        database.setTransactionSuccessful();
                        database.endTransaction();
                    }else {
                        database.endTransaction();
                    }
                }
                else if(strStatus.equals("false")){
                    conStr="3";
                    Globals.responsemessage=strmsg;
                }

                else {
                    database.endTransaction();
                }
            }
            cursor.close();
        } catch (Exception ex) {
            conStr="2";
        }
        return conStr;
    }
//            database.endTransaction();

    private static String send_item_json_on_server(String JsonString,String liccustomerid,String syscode1,String syscode3,String syscode4) {
        String cmpnyId = Globals.Company_Id;
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/contact/data");

        ArrayList nameValuePairs = new ArrayList(8);
        nameValuePairs.add(new BasicNameValuePair("reg_code",Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("data", JsonString));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", syscode1));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", syscode3));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", syscode4));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", liccustomerid));
System.out.println("name value send contact"+nameValuePairs);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
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