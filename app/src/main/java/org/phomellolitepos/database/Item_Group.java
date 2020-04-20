package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.phomellolitepos.CategoryListActivity;
import org.phomellolitepos.LoginActivity;
import org.phomellolitepos.Util.Globals;

public class Item_Group {
    private static String tableName = "item_group";
    private String item_group_id;
    private String device_code;
    private String item_group_code;
    private String parent_code;
    private String item_group_name;
    private String image;
    private String is_active;
    private String modified_by;
    private String modified_date;
    private String is_push;
    private Database db;
    private ContentValues value;

    public Item_Group(Context context, String item_group_id, String device_code,
                      String item_group_code, String parent_code, String item_group_name, String image, String is_active,
                      String modified_by, String modified_date, String is_push) {

        db = new Database(context);
        value = new ContentValues();
        this.set_item_group_id(item_group_id);
        this.set_device_code(device_code);
        this.set_item_group_code(item_group_code);
        this.set_parent_code(parent_code);
        this.set_item_group_name(item_group_name);
        this.set_image(image);
        this.set_is_active(is_active);
        this.set_modified_by(modified_by);
        this.set_modified_date(modified_date);
        this.set_is_push(is_push);
    }

    public String get_item_group_id() {
        return item_group_id;
    }

    public void set_item_group_id(String item_group_id) {
        this.item_group_id = item_group_id;
        value.put("item_group_id", item_group_id);
    }

    public String get_device_code() {
        return device_code;
    }

    public void set_device_code(String device_code) {
        this.device_code = device_code;
        value.put("device_code", device_code);
    }


    public String get_item_group_code() {
        return item_group_code;
    }

    public void set_item_group_code(String item_group_code) {
        this.item_group_code = item_group_code;
        value.put("item_group_code", item_group_code);
    }


    public String get_parent_code() {
        return parent_code;
    }

    public void set_parent_code(String parent_code) {
        this.parent_code = parent_code;
        value.put("parent_code", parent_code);
    }


    public String get_item_group_name() {
        return item_group_name;
    }

    public void set_item_group_name(String item_group_name) {
        this.item_group_name = item_group_name;
        value.put("item_group_name", item_group_name);
    }

    public String get_image() {
        return image;
    }

    public void set_image(String image) {
        this.image = image;
        value.put("image", image);
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

    public String get_modified_date() {
        return modified_date;
    }

    public void set_modified_date(String modified_date) {
        this.modified_date = modified_date;
        value.put("modified_date", modified_date);
    }

    public String get_is_push() {
        return is_push;
    }

    public void set_is_push(String is_push) {
        this.is_push = is_push;
        value.put("is_push", is_push);
    }


    public long insertItem_Group(SQLiteDatabase db) {
        //SQLiteDatabase database = this.db.getWritableDatabase();
        long insert = db.insert(tableName, "item_group_id", value);
        //database.close();
        return insert;
    }


    public long updateItem_Group(String whereClause, String[] whereArgs, SQLiteDatabase db)
            throws SQLiteConstraintException {
        // SQLiteDatabase sdb = this.db.getWritableDatabase();
        long insert = db.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        // sdb.close();
        return insert;
    }

    public static Item_Group getItem_Group(Context context, SQLiteDatabase database, Database db, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Item_Group master = null;
        //Database db = new Database(context);
        //SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Item_Group(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8), cursor.getString(9));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Item_Group> getAllItem_Group(Context context, String WhereClasue, SQLiteDatabase database, Database db) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Item_Group> list = new ArrayList<Item_Group>();
        ArrayList<Item_Group> list_item_code = new ArrayList<Item_Group>();
        Item_Group master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Item_Group(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8), cursor.getString(9));
                list.add(master);

            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }

    public static ArrayList<Item_Group> getAllItem_GroupSpinner_All(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Item_Group> list_spinner = new ArrayList<Item_Group>();
        Item_Group master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Item_Group(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8), cursor.getString(9));
                list_spinner.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list_spinner;
    }

    public static ArrayList<String> getAllItem_GroupCustomList(Context context, String FieldName, String WhereClasue) {
        String Query = "Select " + FieldName + " FROM " + tableName + " " + WhereClasue;
        ArrayList<String> list = new ArrayList<String>();
        Item_Group master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
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

    public static JSONArray convertCursorToJSON(Context context, String strTableQry) {
        JSONArray result = new JSONArray();
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(strTableQry, null);
        try {
            // cursor = db.rawQuery(strTableQry, null);
            int columnCount = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                JSONObject row = new JSONObject();

                for (int index = 0; index < columnCount; index++) {

                    row.put(cursor.getColumnName(index).toLowerCase(), cursor.getString(index));
                }
                result.put(row);
            }
            cursor.close();
        } catch (Exception ex) {
        }
        return result;
    }

    public static String sendOnServer(Context context, SQLiteDatabase database, Database db, String strTableQry,String syscode1,String syscode2,String sycode3,String syscode4, String liccustomerid) {
        String ig = "0";
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        String strItemGroupCode = "";
        Cursor cursor = database.rawQuery(strTableQry, null);
        try {
            // cursor = db.rawQuery(strTableQry, null);
            int columnCount = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                JSONObject sender = new JSONObject();
                JSONArray result = new JSONArray();
                JSONObject row = new JSONObject();
                strItemGroupCode = cursor.getString(2);
                for (int index = 0; index < columnCount; index++) {
                    row.put(cursor.getColumnName(index).toLowerCase(), cursor.getString(index));
                }
                result.put(row);
                sender.put("Item_Group".toLowerCase(), result);
                String serverData = send_item_group_json_on_server(sender.toString(),syscode1,syscode2,sycode3,syscode4,liccustomerid);
                final JSONObject collection_jsonObject1 = new JSONObject(serverData);
                final String strStatus = collection_jsonObject1.getString("status");
                final String strmessage = collection_jsonObject1.getString("message");
                if (strStatus.equals("true")) {
                    database.beginTransaction();
                    String Query = "Update  item_group Set is_push = 'Y' Where item_group_code = '" + strItemGroupCode + "'";
                    long check = db.executeDML(Query, database);
                    if (check > 0) {
                        ig = "1";
                        database.setTransactionSuccessful();
                        database.endTransaction();
                    } else {
                        database.endTransaction();
                    }
                }
               else if(strStatus.equals("false")){
                database.endTransaction();
                ig="0";
               Globals.responsemessage= strmessage;

                }
            }
            cursor.close();
        } catch (Exception ex) {
        }

        return ig;
    }

    private static String send_item_group_json_on_server(String JsonString,String syscode1,String syscode2,String syscode3,String syscode4,String liccustomerid) {
        String cmpnyId = Globals.Company_Id;
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/item_group/data");
        ArrayList nameValuePairs = new ArrayList(8);
        nameValuePairs.add(new BasicNameValuePair("reg_code", Globals.reg_code));
        nameValuePairs.add(new BasicNameValuePair("data", JsonString));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", syscode1));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3", syscode3));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", syscode4));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", liccustomerid));
  System.out.println("namevalue send group"+ nameValuePairs);
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
            System.out.println("response send group"+ serverData);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverData;
    }
}
