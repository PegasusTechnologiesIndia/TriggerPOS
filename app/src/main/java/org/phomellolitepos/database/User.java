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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


import org.phomellolitepos.Util.Globals;

/**
 * Created by Neeraj on 3/2/2017.
 */

public class User {

    private static String tableName = "user";
    private String user_id;
    private String user_group_id;
    private String user_code;
    private String name;
    private String email;
    private String password;
    private String max_discount;
    private String image;
    private String is_active;
    private String Modified_By;
    private String Modified_Date;
    private String Is_Push;
    private String app_user_permission;

    private Database db;
    private ContentValues value;


    public User(Context context, String user_id, String user_group_id, String user_code,
                String name, String email, String password, String max_discount,String image, String is_active, String Modified_By,
                String Modified_Date, String Is_Push,String app_user_permission) {

        db = new Database(context);
        value = new ContentValues();

        this.set_user_id(user_id);
        this.set_user_group_id(user_group_id);
        this.set_user_code(user_code);
        this.set_name(name);
        this.set_email(email);
        this.set_password(password);
        this.set_max_discount(max_discount);
        this.set_image(image);
        this.set_is_active(is_active);
        this.set_Modified_By(Modified_By);
        this.set_Modified_Date(Modified_Date);
        this.set_Is_Push(Is_Push);
        this.set_app_user_permission(app_user_permission);


    }



    public String get_user_id() {
        return user_id;
    }

    public void set_user_id(String user_id) {
        this.user_id = user_id;
        value.put("user_id", user_id);
    }


    public String get_user_group_id() {
        return user_group_id;
    }

    public void set_user_group_id(String user_group_id) {
        this.user_group_id = user_group_id;
        value.put("user_group_id", user_group_id);
    }

    public String get_user_code() {
        return user_code;
    }

    public void set_user_code(String user_code) {
        this.user_code = user_code;
        value.put("user_code", user_code);
    }

    public String get_name() {
        return name;
    }

    public void set_name(String name) {
        this.name = name;
        value.put("name", name);
    }

    public String get_email() {
        return email;
    }

    public void set_email(String email) {
        this.email = email;
        value.put("email", email);
    }

    public String get_password() {
        return password;
    }

    public void set_password(String password) {
        this.password = password;
        value.put("password", password);
    }

    public String get_max_discount() {
        return max_discount;
    }

    public void set_max_discount(String max_discount) {
        this.max_discount = max_discount;
        value.put("max_discount", max_discount);
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

    public String get_Modified_Date() {
        return Modified_Date;
    }

    public void set_Modified_Date(String Modified_Date) {
        this.Modified_Date = Modified_Date;
        value.put("Modified_Date", Modified_Date);
    }

    public String get_Modified_By() {
        return Modified_By;
    }

    public void set_Modified_By(String Modified_By) {
        this.Modified_By = Modified_By;
        value.put("Modified_By", Modified_By);
    }

    public String get_Is_push() {
        return Is_Push;
    }

    public void set_Is_Push(String Is_Push) {
        this.Is_Push = Is_Push;
        value.put("Is_Push", Is_Push);
    }

    public String get_app_user_permission() {
        return app_user_permission;
    }

    public void set_app_user_permission(String app_user_permission) {
        this.app_user_permission = app_user_permission;
        value.put("app_user_permission", app_user_permission);
    }

    public long insertUser(SQLiteDatabase database) {
        //SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "user_id", value);
        //database.close();
        return insert;
    }

    public static long delete_User(Context context, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }

    public long updateUser(String whereClause, SQLiteDatabase database, String[] whereArgs)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        // sdb.close();
        return insert;
    }

    public static User getUser(Context context, String WhereClasue, SQLiteDatabase database) {
        User master = null;
        try {
            String Query = "Select * FROM " + tableName + " " + WhereClasue;

//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery(Query, null);
            if (cursor.moveToFirst()) {
                do {
                    master = new User(context, cursor.getString(0),
                            cursor.getString(1), cursor.getString(2),
                            cursor.getString(3), cursor.getString(4),
                            cursor.getString(5), cursor.getString(6),
                            cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10)
                            , cursor.getString(11), cursor.getString(12));
                } while (cursor.moveToNext());
            }
            cursor.close();
//        database.close();
//        db.close();
        }
        catch (Exception ex)
        {
            String  strError  = ex.getMessage();
            Log.d("User Error: ",strError);
        }
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<User> getAllUser(Context context, String WhereClasue, SQLiteDatabase database, Database db) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<User> list = new ArrayList<User>();
        ArrayList<User> list_item_code = new ArrayList<User>();
        User master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new User(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10)
                        ,cursor.getString(11), cursor.getString(12));
                list.add(master);

            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }

    public static ArrayList<User> getAllUserSpinner_All(Context context, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<User> list_spinner = new ArrayList<User>();
        User master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new User(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10)
                        ,cursor.getString(11), cursor.getString(12));
                list_spinner.add(master);

            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list_spinner;
    }


    public static String sendOnServer(Context context, SQLiteDatabase database, Database db, String strTableQry) {
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        String struserCode = "",resultStr ="0";
        Cursor cursor = database.rawQuery(strTableQry, null);
        try {
            // cursor = db.rawQuery(strTableQry, null);
            int columnCount = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                JSONObject sender = new JSONObject();
                JSONArray result = new JSONArray();
                JSONObject row = new JSONObject();
                struserCode = cursor.getString(2);
                for (int index = 0; index < columnCount; index++) {
                    row.put(cursor.getColumnName(index).toLowerCase(), cursor.getString(index));
                }
                result.put(row);
                sender.put("user".toLowerCase(), result);
                String serverData = send_user_json_on_server(sender.toString());
                final JSONObject jsonObject1 = new JSONObject(serverData);
                final String strStatus = jsonObject1.getString("status");
                if (strStatus.equals("true")) {
                    database.beginTransaction();
                    String Query = "Update  user Set is_push = 'Y' Where user_code = '" + struserCode + "'";
                    long check = db.executeDML(Query, database);
                    if (check > 0) {
                        resultStr="1";
                        database.setTransactionSuccessful();
                        database.endTransaction();
                    } else {
                        database.endTransaction();
                    }
                }
            }
            cursor.close();
        } catch (Exception ex) {
        }
        return resultStr;
    }


    private static String send_user_json_on_server(String JsonString) {
        String cmpnyId = Globals.Company_Id;
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/user/data");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code",Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("data", JsonString));
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

