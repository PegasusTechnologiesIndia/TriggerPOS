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

import org.phomellolitepos.Util.Globals;

/**
 * Created by pegasus-andorid-1 on 1/16/2017.
 */

public class Orders {
    private static String tableName = "orders";
    private String order_id;
    private String device_code;
    private String location_id;
    private String order_type_id;
    private String order_code;
    private String order_date;
    private String contact_code;
    private String emp_code;
    private String total_item;
    private String total_quantity;
    private String sub_total;
    private String total_tax;
    private String total_discount;
    private String total;
    private String tender;
    private String change_amount;
    private String z_code;
    private String load_id;
    private String is_post;
    private String is_active;
    private String modified_by;
    private String modified_date;
    private String is_push;
    private String order_status;
    private String remarks;
    private String table_code;
    private String delivery_date;


    private Database db;
    private ContentValues value;

    public Orders(Context context, String order_id, String device_code,
                  String location_id, String order_type_id, String order_code, String order_date,
                  String contact_code, String emp_code, String total_item,
                  String total_quantity, String sub_total, String total_tax, String total_discount, String total,
                  String tender, String change_amount, String z_code,
                  String load_id, String is_post,
                  String is_active, String modified_by, String modified_date,
                  String is_push, String order_status, String remarks, String table_code, String delivery_date) {

        db = new Database(context);
        value = new ContentValues();

        this.set_order_id(order_id);
        this.set_device_code(device_code);
        this.set_location_id(location_id);
        this.set_order_type_id(order_type_id);
        this.set_order_code(order_code);
        this.set_order_date(order_date);
        this.set_contact_code(contact_code);
        this.set_emp_code(emp_code);
        this.set_total_item(total_item);
        this.set_total_quantity(total_quantity);
        this.set_sub_total(sub_total);
        this.set_total_tax(total_tax);
        this.set_total_discount(total_discount);
        this.set_total(total);
        this.set_tender(tender);
        this.set_change_amount(change_amount);
        this.set_z_code(z_code);
        this.set_load_id(load_id);
        this.set_is_post(is_post);
        this.set_is_active(is_active);
        this.set_modified_by(modified_by);
        this.set_modified_date(modified_date);
        this.set_is_push(is_push);
        this.set_order_status(order_status);
        this.set_remarks(remarks);
        this.set_table_code(table_code);
        this.set_delivery_date(delivery_date);

    }


    public String get_order_id() {
        return order_id;
    }

    public void set_order_id(String order_id) {
        this.order_id = order_id;
        value.put("order_id", order_id);
    }

    public String get_device_code() {
        return device_code;
    }

    public void set_device_code(String device_code) {
        this.device_code = device_code;
        value.put("device_code", device_code);
    }

    public String get_location_id() {
        return location_id;
    }

    public void set_location_id(String location_id) {
        this.location_id = location_id;
        value.put("location_id", location_id);
    }

    public String get_order_type_id() {
        return order_type_id;
    }

    public void set_order_type_id(String order_type_id) {
        this.order_type_id = order_type_id;
        value.put("order_type_id", order_type_id);
    }

    public String get_order_code() {
        return order_code;
    }

    public void set_order_code(String order_code) {
        this.order_code = order_code;
        value.put("order_code", order_code);
    }


    public String get_order_date() {
        return order_date;
    }

    public void set_order_date(String order_date) {
        this.order_date = order_date;
        value.put("order_date", order_date);
    }

    public String get_contact_code() {
        return contact_code;
    }

    public void set_contact_code(String contact_code) {
        this.contact_code = contact_code;
        value.put("contact_code", contact_code);
    }

    public String get_emp_code() {
        return emp_code;
    }

    public void set_emp_code(String emp_code) {
        this.emp_code = emp_code;
        value.put("emp_code", emp_code);
    }

    public String get_total_item() {
        return total_item;
    }

    public void set_total_item(String total_item) {
        this.total_item = total_item;
        value.put("total_item", total_item);
    }

    public String get_total_quantity() {
        return total_quantity;
    }

    public void set_total_quantity(String total_quantity) {
        this.total_quantity = total_quantity;
        value.put("total_quantity", total_quantity);
    }

    public String get_sub_total() {
        return sub_total;
    }

    public void set_sub_total(String sub_total) {
        this.sub_total = sub_total;
        value.put("sub_total", sub_total);
    }

    public String get_total_tax() {
        return total_tax;
    }

    public void set_total_tax(String total_tax) {
        this.total_tax = total_tax;
        value.put("total_tax", total_tax);
    }

    public String get_total_discount() {
        return total_discount;
    }

    public void set_total_discount(String total_discount) {
        this.total_discount = total_discount;
        value.put("total_discount", total_discount);
    }

    public String get_total() {
        return total;
    }

    public void set_total(String total) {
        this.total = total;
        value.put("total", total);
    }

    public String get_tender() {
        return tender;
    }

    public void set_tender(String tender) {
        this.tender = tender;
        value.put("tender", tender);
    }

    public String get_change_amount() {
        return change_amount;
    }

    public void set_change_amount(String change_amount) {
        this.change_amount = change_amount;
        value.put("change_amount", change_amount);
    }

    public String get_z_code() {
        return z_code;
    }

    public void set_z_code(String z_code) {
        this.z_code = z_code;
        value.put("z_code", z_code);

    }

    public String get_load_id() {
        return load_id;
    }

    public void set_load_id(String load_id) {
        this.load_id = load_id;
        value.put("load_id", load_id);
    }

    public String get_is_post() {
        return is_post;
    }

    public void set_is_post(String is_post) {
        this.is_post = is_post;
        value.put("is_post", is_post);
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


    public String get_order_status() {
        return order_status;
    }

    public void set_order_status(String order_status) {
        this.order_status = order_status;
        value.put("order_status", order_status);
    }

    public String get_remarks() {
        return remarks;
    }

    public void set_remarks(String remarks) {
        this.remarks = remarks;
        value.put("remarks", remarks);
    }

    public String get_table_code() {
        return table_code;
    }

    public void set_table_code(String table_code) {
        this.table_code = table_code;
        value.put("table_code", table_code);
    }

    public String get_delivery_date() {
        return delivery_date;
    }

    public void set_delivery_date(String delivery_date) {
        this.delivery_date = delivery_date;
        value.put("delivery_date", delivery_date);
    }

    public long insertOrders(SQLiteDatabase database) {
        // SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "order_id", value);
        //database.close();
        return insert;
    }

    public static long delete_orders(Context context, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }

    public long updateOrders(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        // sdb.close();
        return insert;
    }

    public static Orders getOrders(Context context, SQLiteDatabase database, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Orders master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {

                master = new Orders(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10),
                        cursor.getString(11), cursor.getString(12),
                        cursor.getString(13), cursor.getString(14),
                        cursor.getString(15), cursor.getString(16),
                        cursor.getString(17), cursor.getString(18),
                        cursor.getString(19), cursor.getString(20), cursor.getString(21),
                        cursor.getString(22), cursor.getString(23),
                        cursor.getString(24), cursor.getString(25), cursor.getString(26));

            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }

    // Here Changed in function  need to update all classes
    public static ArrayList<Orders> getAllOrders(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Orders> list = new ArrayList<Orders>();
        Orders master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Orders(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10),
                        cursor.getString(11), cursor.getString(12),
                        cursor.getString(13), cursor.getString(14),
                        cursor.getString(15), cursor.getString(16),
                        cursor.getString(17), cursor.getString(18),
                        cursor.getString(19), cursor.getString(20),
                        cursor.getString(21), cursor.getString(22),
                        cursor.getString(23), cursor.getString(24),
                        cursor.getString(25), cursor.getString(26));

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // database.close();
        //db.close();
        return list;
    }

    public static ArrayList<Orders> getAllOrdersSearch(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select orders.* FROM " + tableName + " " + WhereClasue;
        ArrayList<Orders> list = new ArrayList<Orders>();
        Orders master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Orders(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10),
                        cursor.getString(11), cursor.getString(12),
                        cursor.getString(13), cursor.getString(14),
                        cursor.getString(15), cursor.getString(16),
                        cursor.getString(17), cursor.getString(18),
                        cursor.getString(19), cursor.getString(20),
                        cursor.getString(21), cursor.getString(22),
                        cursor.getString(23), cursor.getString(24),
                        cursor.getString(25), cursor.getString(26));

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // database.close();
        //db.close();
        return list;
    }


    // Here Changed in function  need to update all classes
    public static ArrayList<Orders> getAllOrdersforstickyList(Context context, String WhereClasue) {
        String Query = "select count(*) ,SUM(total) from orders" + tableName + " " + WhereClasue;
        ArrayList<Orders> list = new ArrayList<Orders>();
        Orders master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Orders(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10),
                        cursor.getString(11), cursor.getString(12),
                        cursor.getString(13), cursor.getString(14),
                        cursor.getString(15), cursor.getString(16),
                        cursor.getString(17), cursor.getString(18),
                        cursor.getString(19), cursor.getString(20),
                        cursor.getString(21), cursor.getString(22),
                        cursor.getString(23), cursor.getString(24),
                        cursor.getString(25), cursor.getString(26));

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // database.close();
        // db.close();
        return list;
    }

    public static String sendOnServer(Context context, SQLiteDatabase database, Database db, String strTableQry) {
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReada,bleDatabase();
        String strOrder_Code = "", resultStr = "0";
        Cursor cursor = database.rawQuery(strTableQry, null);
        try {
            int columnCount = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                JSONObject sender = new JSONObject();
                JSONArray result = new JSONArray();
                JSONObject row = new JSONObject();
                JSONArray array_order_detail = new JSONArray();
                JSONObject order_detail_row = new JSONObject();
                JSONArray array_order_payment = new JSONArray();
                JSONObject order_payment_row = new JSONObject();
                JSONArray array_order_tax = new JSONArray();
                JSONObject order_order_tax_row = new JSONObject();
                JSONArray array_order_detail_tax = new JSONArray();
                JSONObject order_detail_tax_row = new JSONObject();
                JSONArray array_order_cus_debit = new JSONArray();
                JSONObject order_cus_debit = new JSONObject();

                strOrder_Code = cursor.getString(4);
                for (int index = 0; index < columnCount; index++) {
                    row.put(cursor.getColumnName(index).toLowerCase(), cursor.getString(index));
                }
                String Order_detail_qry = "SELECT * FROM  order_detail Where  order_code = '" + strOrder_Code + "'";
                Cursor cursor_order_detail = database.rawQuery(Order_detail_qry, null);
                try {
                    int columnCount_order_detail = cursor_order_detail.getColumnCount();
                    while (cursor_order_detail.moveToNext()) {
                        order_detail_row = new JSONObject();
                        for (int index = 0; index < columnCount_order_detail; index++) {
                            order_detail_row.put(cursor_order_detail.getColumnName(index).toLowerCase(), cursor_order_detail.getString(index));
                        }
                        array_order_detail.put(order_detail_row);
                    }
                    cursor_order_detail.close();
                } catch (Exception ex) {
                }
                String Order_payment_qry = "SELECT * FROM  order_payment Where  order_code = '" + strOrder_Code + "'";
                Cursor cursor_order_payment = database.rawQuery(Order_payment_qry, null);
                try {
                    int columnCount_order_payment = cursor_order_payment.getColumnCount();
                    while (cursor_order_payment.moveToNext()) {
                        order_payment_row = new JSONObject();
                        for (int index = 0; index < columnCount_order_payment; index++) {
                            order_payment_row.put(cursor_order_payment.getColumnName(index).toLowerCase(), cursor_order_payment.getString(index));
                        }
                        array_order_payment.put(order_payment_row);
                    }
                    cursor_order_payment.close();
                } catch (Exception ex) {
                }

                String Order_od_tax_qry = "SELECT * FROM  order_detail_tax Where  order_code = '" + strOrder_Code + "'";
                Cursor cursor_od_tax = database.rawQuery(Order_od_tax_qry, null);
                try {
                    int columnCount_order_payment = cursor_od_tax.getColumnCount();
                    while (cursor_od_tax.moveToNext()) {
                        order_detail_tax_row = new JSONObject();
                        for (int index = 0; index < columnCount_order_payment; index++) {
                            order_detail_tax_row.put(cursor_od_tax.getColumnName(index).toLowerCase(), cursor_od_tax.getString(index));
                        }
                        array_order_detail_tax.put(order_detail_tax_row);
                    }
                    cursor_od_tax.close();
                } catch (Exception ex) {}

                String Order_or_tax_qry = "SELECT * FROM  order_tax Where  order_code = '" + strOrder_Code + "'";
                Cursor cursor_odr_tax = database.rawQuery(Order_or_tax_qry, null);
                try {
                    int columnCount_order_payment = cursor_odr_tax.getColumnCount();
                    while (cursor_odr_tax.moveToNext()) {
                        order_order_tax_row = new JSONObject();
                        for (int index = 0; index < columnCount_order_payment; index++) {
                            order_order_tax_row.put(cursor_odr_tax.getColumnName(index).toLowerCase(), cursor_odr_tax.getString(index));
                        }
                        array_order_tax.put(order_order_tax_row);
                    }
                    cursor_odr_tax.close();
                } catch (Exception ex) {
                }

                String Order_cus_debit_qry = "SELECT * FROM  acc_customer_dedit Where  order_code = '" + strOrder_Code + "'";
                Cursor cursor_cus_debit = database.rawQuery(Order_cus_debit_qry, null);
                try {
                    int columnCount_order_payment = cursor_cus_debit.getColumnCount();
                    while (cursor_cus_debit.moveToNext()) {
                        order_cus_debit = new JSONObject();
                        for (int index = 0; index < columnCount_order_payment; index++) {
                            order_cus_debit.put(cursor_cus_debit.getColumnName(index).toLowerCase(), cursor_cus_debit.getString(index));
                        }
                        array_order_cus_debit.put(order_cus_debit);
                    }
                    cursor_cus_debit.close();
                } catch (Exception ex) {
                }

                row.put("order_detail", array_order_detail);
                row.put("order_payment", array_order_payment);
                row.put("order_tax", array_order_tax);
                row.put("order_detail_tax", array_order_detail_tax);
                row.put("order_customer_debit", array_order_cus_debit);
                result.put(row);
                sender.put("order".toLowerCase(), result);
                String serverData = send_item_json_on_server(sender.toString());
                final JSONObject collection_jsonObject1 = new JSONObject(serverData);
                final String strStatus = collection_jsonObject1.getString("status");
                if (strStatus.equals("true")) {
                    database.beginTransaction();
                    // Update This Item Group Push True
                    String Query = "Update  orders Set is_push = 'Y' Where order_code = '" + strOrder_Code + "'";
                    long or_result = db.executeDML(Query, database);
                    if (or_result > 0) {
                        resultStr = "1";
                        database.setTransactionSuccessful();
                        database.endTransaction();
                    } else {

                    }
                }
            }
            cursor.close();
        } catch (Exception ex) {
            String strExp = ex.getMessage();
        }
        return resultStr;
    }

    private static String send_item_json_on_server(String JsonString) {
        String cmpnyId = Globals.Company_Id;
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                "http://" + Globals.App_IP + "/lite-pos-lic/index.php/api/order/data");
        ArrayList nameValuePairs = new ArrayList(5);
        nameValuePairs.add(new BasicNameValuePair("reg_code",Globals.objLPR.getRegistration_Code()));
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
}
