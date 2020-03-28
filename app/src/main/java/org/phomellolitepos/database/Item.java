package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.itextpdf.text.pdf.codec.Base64;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


/**
 * Created by pegasus-andorid-1 on 1/16/2017.
 */

public class Item {

    private static String tableName = "item";
    private String item_id;
    private String device_code;
    private String item_code;
    private String parent_code;
    private String item_group_code;
    private String manufacture_code;
    private String item_name;
    private String description;
    private String sku;
    private String barcode;
    private String hsn_sac_code;
    private String image;
    private String item_type;
    private String unit_id;
    private String is_return_stockable;
    private String is_service;
    private String is_active;
    private String modified_by;
    private String modified_date;
    private String is_push;
    private String is_inclusive_tax;
    private String item_image;

    private Database db;
    private ContentValues value;


    public Item(Context context, String item_id, String device_code,
                String item_code, String parent_code, String item_group_code, String manufacture_code,
                String item_name, String description, String sku, String barcode, String hsn_sac_code, String image,
                String item_type, String unit_id, String is_return_stockable, String is_service, String is_active, String modified_by,
                String modified_date, String is_push, String is_inclusive_tax, String item_image) {

        db = new Database(context);
        value = new ContentValues();

        this.set_item_id(item_id);
        this.set_device_code(device_code);
        this.set_item_code(item_code);
        this.set_parent_code(parent_code);
        this.set_item_group_code(item_group_code);
        this.set_manufacture_code(manufacture_code);
        this.set_item_name(item_name);
        this.set_description(description);
        this.set_sku(sku);
        this.set_barcode(barcode);
        this.set_hsn_sac_code(hsn_sac_code);
        this.set_image(image);
        this.set_item_type(item_type);
        this.set_unit_id(unit_id);
        this.set_is_return_stockable(is_return_stockable);
        this.set_is_service(is_service);
        this.set_is_active(is_active);
        this.set_modified_by(modified_by);
        this.set_modified_date(modified_date);
        this.set_is_push(is_push);
        this.set_is_inclusive_tax(is_inclusive_tax);
        this.set_item_image(item_image);

    }

    public String get_item_id() {
        return item_id;
    }

    public void set_item_id(String item_id) {
        this.item_id = item_id;
        value.put("item_id", item_id);
    }


    public String get_device_code() {
        return device_code;
    }

    public void set_device_code(String device_code) {
        this.device_code = device_code;
        value.put("device_code", device_code);
    }

    public String get_item_code() {
        return item_code;
    }

    public void set_item_code(String item_code) {
        this.item_code = item_code;
        value.put("item_code", item_code);
    }

    public String get_parent_code() {
        return parent_code;
    }

    public void set_parent_code(String parent_code) {
        this.parent_code = parent_code;
        value.put("parent_code", parent_code);
    }

    public String get_item_group_code() {
        return item_group_code;
    }

    public void set_item_group_code(String item_group_code) {
        this.item_group_code = item_group_code;
        value.put("item_group_code", item_group_code);
    }

    public String get_manufacture_code() {
        return manufacture_code;
    }

    public void set_manufacture_code(String manufacture_code) {
        this.manufacture_code = manufacture_code;
        value.put("manufacture_code", manufacture_code);
    }

    public String get_item_name() {
        return item_name;
    }

    public void set_item_name(String item_name) {
        this.item_name = item_name;
        value.put("item_name", item_name);
    }

    public String get_description() {
        return description;
    }

    public void set_description(String description) {
        this.description = description;
        value.put("description", description);
    }

    public String get_sku() {
        return sku;
    }

    public void set_sku(String sku) {
        this.sku = sku;
        value.put("sku", sku);
    }

    public String get_barcode() {
        return barcode;
    }

    public void set_barcode(String barcode) {
        this.barcode = barcode;
        value.put("barcode", barcode);
    }


    public String get_hsn_sac_code() {
        return hsn_sac_code;
    }

    public void set_hsn_sac_code(String hsn_sac_code) {
        this.hsn_sac_code = hsn_sac_code;
        value.put("hsn_sac_code", hsn_sac_code);
    }

    public String get_image() {
        return image;
    }

    public void set_image(String image) {
        this.image = image;
        value.put("image", image);
    }

    public String get_item_type() {
        return item_type;
    }

    public void set_item_type(String item_type) {
        this.item_type = item_type;
        value.put("item_type", item_type);
    }

    public String get_unit_id() {
        return unit_id;
    }

    public void set_unit_id(String unit_id) {
        this.unit_id = unit_id;
        value.put("unit_id", unit_id);
    }

    public String get_is_return_stockable() {
        return is_return_stockable;
    }

    public void set_is_return_stockable(String is_return_stockable) {
        this.is_return_stockable = is_return_stockable;
        value.put("is_return_stockable", is_return_stockable);
    }

    public String get_is_service() {
        return is_service;
    }

    public void set_is_service(String is_service) {
        this.is_service = is_service;
        value.put("is_service", is_service);
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

    public String get_is_inclusive_tax() {
        return is_inclusive_tax;
    }

    public void set_is_inclusive_tax(String is_inclusive_tax) {
        this.is_inclusive_tax = is_inclusive_tax;
        value.put("is_inclusive_tax", is_inclusive_tax);
    }


    public String get_item_image() {
        return item_image;
    }

    public void set_item_image(String item_image) {
        this.item_image = item_image;
        value.put("item_image", item_image);
    }


    public long insertItem(SQLiteDatabase database) {
//        SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "item_id", value);
        return insert;
    }

    public long updateItem(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public static Item getItem(Context context, String WhereClasue, SQLiteDatabase database, Database db) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Item master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Item(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10),
                        cursor.getString(11), cursor.getString(12),
                        cursor.getString(13), cursor.getString(14),
                        cursor.getString(15), cursor.getString(16),
                        cursor.getString(17), cursor.getString(18),
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
    public static ArrayList<Item> getAllItem(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Item> list = new ArrayList<Item>();
        Item master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                try {


                    master = new Item(context, cursor.getString(0),
                            cursor.getString(1), cursor.getString(2),
                            cursor.getString(3), cursor.getString(4),
                            cursor.getString(5), cursor.getString(6),
                            cursor.getString(7), cursor.getString(8),
                            cursor.getString(9),
                            cursor.getString(10), cursor.getString(11),
                            cursor.getString(12), cursor.getString(13),
                            cursor.getString(14), cursor.getString(15),
                            cursor.getString(16), cursor.getString(17),
                            cursor.getString(18), cursor.getString(19),
                            cursor.getString(20),
                            cursor.getString(21));
                    list.add(master);
                } catch (Exception ex) {

                }
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }


    public static ArrayList<String> getAllItemforautocomplete(Context context, String WhereClasue) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            String Query = "Select * FROM " + tableName + " " + WhereClasue;


            Database db = new Database(context);
            SQLiteDatabase database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery(Query, null);
            if (cursor.moveToFirst()) {
                do {
                    list.add(cursor.getString(6));
                } while (cursor.moveToNext());
            }

        } catch (Exception ex) {
        }
        //cursor.close();
//        database.close();
//        db.close();
        return list;

    }


    public static String sendOnServer(Context context, SQLiteDatabase database, Database db, String strTableQry) {

        String strItemCode = "", resultStr = "0";
        Cursor cursor = database.rawQuery(strTableQry, null);
        try {
            int columnCount = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                JSONObject sender = new JSONObject();
                JSONArray result = new JSONArray();
                JSONObject row = new JSONObject();
                JSONArray array_item_location = new JSONArray();
                JSONObject item_location_row = new JSONObject();
                JSONArray array_item_location_tax = new JSONArray();
                JSONArray array_item_supplier = new JSONArray();
                JSONObject item_item_supplier = new JSONObject();
                JSONArray array_item_tax = new JSONArray();
                JSONObject item_item_tax = new JSONObject();
                strItemCode = cursor.getString(1);
                for (int index = 0; index < columnCount; index++) {
                    if (index==9){
                        try {
                        Uri uri = Uri.fromFile(new File(cursor.getString(index)));
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                        String strImage = BitmapToString(bitmap);
                        row.put(cursor.getColumnName(index).toLowerCase(),strImage);
                        }catch (Exception ex){
                            row.put(cursor.getColumnName(index).toLowerCase(),"");
                        }
                    }else {
                        row.put(cursor.getColumnName(index).toLowerCase(),cursor.getString(index));
                    }

                }

                String item_location_qry = "Select location_id , item_code,cost_price,markup,selling_price,quantity,loyalty_point,reorder_point,reorder_amount,is_inventory_tracking,is_active,modified_by,new_sell_price FROM item_location WHERE item_code  = '" + strItemCode + "'";
                Cursor cursor_location = database.rawQuery(item_location_qry, null);
                try {
                    int columnCount_location = cursor_location.getColumnCount();
                    while (cursor_location.moveToNext()) {

                        for (int index = 0; index < columnCount_location; index++) {
                            item_location_row.put(cursor_location.getColumnName(index).toLowerCase(), cursor_location.getString(index));
                        }
                        array_item_location.put(item_location_row);
                    }
                    cursor_location.close();

                    String item_supplier_qry = "Select item_code, contact_code from item_supplier where item_code  = '" + strItemCode + "'";
                    Cursor cursor_item_supplier = database.rawQuery(item_supplier_qry, null);
                    try {
                        int columnCount_supplier = cursor_item_supplier.getColumnCount();
                        while (cursor_item_supplier.moveToNext()) {
                            item_item_supplier = new JSONObject();
                            for (int index = 0; index < columnCount_supplier; index++) {
                                item_item_supplier.put(cursor_item_supplier.getColumnName(index).toLowerCase(), cursor_item_supplier.getString(index));
                            }
                            array_item_supplier.put(item_item_supplier);
                        }

                    } catch (Exception ex) {}

                    String item_tax_qry = "Select item_group_code, tax_id ,location_id from item_group_tax where item_group_code  = '" + strItemCode + "'";
                    Cursor cursor_item_tax = database.rawQuery(item_tax_qry, null);
                    try {
                        int columnCount_item_tax = cursor_item_tax.getColumnCount();
                        while (cursor_item_tax.moveToNext()) {
                            item_item_tax = new JSONObject();
                            for (int index = 0; index < columnCount_item_tax; index++) {
                                item_item_tax.put(cursor_item_tax.getColumnName(index).toLowerCase(), cursor_item_tax.getString(index));
                            }
                            array_item_tax.put(item_item_tax);
                        }

                    } catch (Exception ex) {
                    }

                    row.put("item_location", array_item_location);
                    row.put("item_supplier", array_item_supplier);
                    row.put("item_tax", array_item_tax);
                    result.put(row);
                    sender.put("item".toLowerCase(), result);
                    String serverData = send_item_json_on_server(sender.toString());
                    final JSONObject collection_jsonObject1 = new JSONObject(serverData);
                    final String strStatus = collection_jsonObject1.getString("status");
                    if (strStatus.equals("true")) {
                        // Update This Item Group Push True
                        database.beginTransaction();
                        String Query = "Update  item Set is_push = 'Y' Where item_code = '" + strItemCode + "'";
                        long check = db.executeDML(Query, database);
                        if (check > 0) {
                            resultStr = "1";
                            database.setTransactionSuccessful();
                            database.endTransaction();
                        } else {
                            database.endTransaction();
                        }
                    } else {
                        resultStr = "2";
                        database.endTransaction();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                cursor.close();
            }
        } catch (Exception ex) {
            String msg = ex.getMessage();
        }
        return resultStr;
    }

    private static String send_item_json_on_server(String JsonString) {
        String cmpnyId = Globals.Company_Id;
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/item/data");

        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code",Globals.reg_code));
        nameValuePairs.add(new BasicNameValuePair("data", JsonString));
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

    public static String BitmapToString(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 60, baos);
            byte[] b = baos.toByteArray();
            String temp = Base64.encodeBytes(b, Base64.ENCODE);
            return temp;
        } catch (NullPointerException e) {
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }
}
