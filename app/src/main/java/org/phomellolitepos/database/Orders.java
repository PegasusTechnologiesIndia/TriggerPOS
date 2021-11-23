package org.phomellolitepos.database;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
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
import java.util.HashMap;
import java.util.Map;

import org.phomellolitepos.AccountsActivity;
import org.phomellolitepos.AppController;
import org.phomellolitepos.LoginActivity;
import org.phomellolitepos.MainActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.X_ZActivity;

import in.gauriinfotech.commons.Progress;

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
    private String RFID;
    String result;
    private Database db;
    private ContentValues value;

    public Orders(Context context, String order_id, String device_code,
                  String location_id, String order_type_id, String order_code, String order_date,
                  String contact_code, String emp_code, String total_item,
                  String total_quantity, String sub_total, String total_tax, String total_discount, String total,
                  String tender, String change_amount, String z_code,
                  String load_id, String is_post,
                  String is_active, String modified_by, String modified_date,
                  String is_push, String order_status, String remarks, String table_code, String delivery_date,String rfid) {

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
        this.setRFID(rfid);

    }

    public Orders(Context ctx){
        db = new Database(ctx);
        value = new ContentValues();

    }
    public Orders(Context ctx,String Orderid){
        db = new Database(ctx);
        value = new ContentValues();
        this.set_order_id(Orderid);
    }

    public String getRFID() {
        return RFID;
    }

    public void setRFID(String RFID) {
        this.RFID = RFID;
        value.put("RFID", RFID);
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

    public static long delete_orders(Context context, String orders, String whereClause, String[] whereArgs, SQLiteDatabase database) {
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
                        cursor.getString(24), cursor.getString(25), cursor.getString(26),cursor.getString(27));

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
                        cursor.getString(25), cursor.getString(26),cursor.getString(27));

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
                        cursor.getString(25), cursor.getString(26),cursor.getString(27));

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
                        cursor.getString(25), cursor.getString(26),cursor.getString(27));

                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // database.close();
        // db.close();
        return list;
    }
    public String response_var="0";
    public static String sendOn_Server(Context context, SQLiteDatabase database, Database db, String strTableQry,String liccustomerid,String syscode1,String syscode3, String Syscode4) {
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReada,bleDatabase();
        String strOrder_Code = "", strContactCode="",resultStr = "0";
        Cursor cursor = database.rawQuery(strTableQry, null);

        try {
            int columnCount = cursor.getColumnCount();
             ArrayList<String> list=new ArrayList<String>();
            JSONObject sender=null;

            while (cursor.moveToNext()) {
                sender =  new JSONObject();

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
                JSONArray customer_array = new JSONArray();
                JSONObject order_customer_obj = new JSONObject();
                strOrder_Code = cursor.getString(4);
                strContactCode=cursor.getString(6);
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

                // Customer array
                String Order_cus_qry = "SELECT * FROM  contact Where  contact_code = '" + strContactCode + "'";
                Cursor cursor_customer = database.rawQuery(Order_cus_qry, null);
                try {
                    int columnCount_order_payment = cursor_customer.getColumnCount();
                    while (cursor_customer.moveToNext()) {
                        order_customer_obj = new JSONObject();
                        for (int index = 0; index < columnCount_order_payment; index++) {
                            order_customer_obj.put(cursor_customer.getColumnName(index).toLowerCase(), cursor_customer.getString(index));
                        }
                        customer_array.put(order_customer_obj);
                    }
                    cursor_customer.close();
                } catch (Exception ex) {
                }

          /*      String return_qry = "SELECT * FROM  returns Where is_push='N'";
                Cursor return_qry_cursor = database.rawQuery(return_qry, null);
                try {
                    int columnCount_order_return = return_qry_cursor.getColumnCount();
                    while (return_qry_cursor.moveToNext()) {
                        order_array_return = new JSONObject();
                        for (int index = 0; index < columnCount_order_return; index++) {
                            order_array_return.put(return_qry_cursor.getColumnName(index).toLowerCase(), return_qry_cursor.getString(index));
                        }
                        array_return.put(order_array_return);
                    }
                    return_qry_cursor.close();
                } catch (Exception ex) {
                }*/

                row.put("order_detail", array_order_detail);
                row.put("order_payment", array_order_payment);
                row.put("order_tax", array_order_tax);
                row.put("order_detail_tax", array_order_detail_tax);
                row.put("order_customer_debit", array_order_cus_debit);
                row.put("order_customerContact_array", customer_array);
              //  row.put("order_return_array", array_return);
                result.put(row);
                list.add(result.toString());
                sender.put("order".toLowerCase(), result);
               // send_item_json_on_server(context,database,db,sender.toString(),strOrder_Code,syscode1,syscode3,Syscode4,liccustomerid);

                String serverData = send_item_json_on_server(sender.toString(),liccustomerid,syscode1,syscode3,Syscode4);
                final JSONObject collection_jsonObject1 = new JSONObject(serverData);
                final String strStatus = collection_jsonObject1.getString("status");
                final String strmsg = collection_jsonObject1.getString("message");
                if (strStatus.equals("true"))
                {
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
                /*    database.beginTransaction();
                    String Query1 = "Update  returns Set is_push = 'Y' and is_post='true'";
                    long or_result1 = db.executeDML(Query1, database);
                    if (or_result1 > 0) {
                        resultStr = "3";

                        database.setTransactionSuccessful();
                        database.endTransaction();
                    } else {

                    }*/


                }
                else if(strStatus.equals("false")){
                    resultStr = "2";
                    Globals.responsemessage=strmsg;

                }

            }
            cursor.close();
        } catch (Exception ex) {
            String strExp = ex.getMessage();
        }
        return resultStr;
            }


  /*  public static void sendOnServer(Context context, SQLiteDatabase database, Database db, String strTableQry,String liccustomerid,String syscode1,String syscode3, String Syscode4) {
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReada,bleDatabase();
        String strOrder_Code = "", strContactCode="",resultStr = "0";
        Cursor cursor = database.rawQuery(strTableQry, null);
        try {
            int columnCount = cursor.getColumnCount();
            ArrayList<String> list=new ArrayList<String>();
            JSONObject sender=null;

            while (cursor.moveToNext()) {
                sender =  new JSONObject();

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
                JSONArray customer_array = new JSONArray();
                JSONObject order_customer_obj = new JSONObject();
                strOrder_Code = cursor.getString(4);
                strContactCode=cursor.getString(6);
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

                // Customer array
                String Order_cus_qry = "SELECT * FROM  contact Where  contact_code = '" + strContactCode + "'";
                Cursor cursor_customer = database.rawQuery(Order_cus_qry, null);
                try {
                    int columnCount_order_payment = cursor_customer.getColumnCount();
                    while (cursor_customer.moveToNext()) {
                        order_customer_obj = new JSONObject();
                        for (int index = 0; index < columnCount_order_payment; index++) {
                            order_customer_obj.put(cursor_customer.getColumnName(index).toLowerCase(), cursor_customer.getString(index));
                        }
                        customer_array.put(order_customer_obj);
                    }
                    cursor_customer.close();
                } catch (Exception ex) {
                }

          *//*      String return_qry = "SELECT * FROM  returns Where is_push='N'";
                Cursor return_qry_cursor = database.rawQuery(return_qry, null);
                try {
                    int columnCount_order_return = return_qry_cursor.getColumnCount();
                    while (return_qry_cursor.moveToNext()) {
                        order_array_return = new JSONObject();
                        for (int index = 0; index < columnCount_order_return; index++) {
                            order_array_return.put(return_qry_cursor.getColumnName(index).toLowerCase(), return_qry_cursor.getString(index));
                        }
                        array_return.put(order_array_return);
                    }
                    return_qry_cursor.close();
                } catch (Exception ex) {
                }*//*

                row.put("order_detail", array_order_detail);
                row.put("order_payment", array_order_payment);
                row.put("order_tax", array_order_tax);
                row.put("order_detail_tax", array_order_detail_tax);
                row.put("order_customer_debit", array_order_cus_debit);
                row.put("order_customerContact_array", customer_array);
                //  row.put("order_return_array", array_return);
                result.put(row);
                list.add(result.toString());
                sender.put("order".toLowerCase(), result);
                // send_item_json_on_server(context,database,db,sender.toString(),strOrder_Code,syscode1,syscode3,Syscode4,liccustomerid);

                String serverData = send_item_json_on_server(sender.toString(),liccustomerid,syscode1,syscode3,Syscode4);
                final JSONObject collection_jsonObject1 = new JSONObject(serverData);
                final String strStatus = collection_jsonObject1.getString("status");
                final String strmsg = collection_jsonObject1.getString("message");
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
                *//*    database.beginTransaction();
                    String Query1 = "Update  returns Set is_push = 'Y' and is_post='true'";
                    long or_result1 = db.executeDML(Query1, database);
                    if (or_result1 > 0) {
                        resultStr = "3";

                        database.setTransactionSuccessful();
                        database.endTransaction();
                    } else {

                    }*//*

                }
                else if(strStatus.equals("false")){
                    resultStr = "2";
                    Globals.responsemessage=strmsg;

                }



            }
            cursor.close();
        } catch (Exception ex) {
            String strExp = ex.getMessage();
        }
        //return resultStr;
    }
*/
    //  return resultStr;


    private static String send_item_json_on_server(String JsonString,String liccustomerid,String syscode1,String syscode3,String syscode4) {
        String cmpnyId = Globals.Company_Id;
        String serverData = null;//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(
                Globals.App_IP_URL + "order/data");
        ArrayList nameValuePairs = new ArrayList(8);
        nameValuePairs.add(new BasicNameValuePair("reg_code",Globals.objLPR.getRegistration_Code()));
        nameValuePairs.add(new BasicNameValuePair("data", JsonString));
        nameValuePairs.add(new BasicNameValuePair("sys_code_1", syscode1));
        nameValuePairs.add(new BasicNameValuePair("sys_code_2", Globals.syscode2));
        nameValuePairs.add(new BasicNameValuePair("sys_code_3",syscode3));
        nameValuePairs.add(new BasicNameValuePair("sys_code_4", syscode4));
        nameValuePairs.add(new BasicNameValuePair("device_code", Globals.Device_Code));
        nameValuePairs.add(new BasicNameValuePair("lic_customer_license_id", liccustomerid));

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


   /* public  static  void send_item_json_on_server(Context context, SQLiteDatabase database, Database db,  final String JsonString, String strOrder_Code, final String syscode1, final String syscode3, final String syscode4, final String liccustomerid) {

  ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.Syncingh));
        pDialog.show();

        String server_url = Globals.App_IP_URL + "order/data";
        //HttpsTrustManager.allowAllSSL();
        // String server_url =  Gloabls.server_url;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            final JSONObject collection_jsonObject1 = new JSONObject(response);
                            final String strStatus = collection_jsonObject1.getString("status");
                            final String strmsg = collection_jsonObject1.getString("message");
                            if (strStatus.equals("true")) {
                                database.beginTransaction();
                                // Update This Item Group Push True
                                String Query = "Update  orders Set is_push = 'Y' Where order_code = '" + strOrder_Code + "'";
                                long or_result = db.executeDML(Query, database);

                                if (or_result > 0) {
                                    //resultStr = "1";
                                   // response_var="1";
                                    Globals.orderresponsemessage="1";
                                    Pos_Balance posbalance=new Pos_Balance(context);
                                    String result = Pos_Balance.sendOnServer(pDialog,context, database, db, "SELECT device_code,z_code,date,total_amount,is_active,modified_by FROM  z_close Where is_push = 'N'", Globals.license_id, syscode1, syscode3, syscode4);
                                    ((X_ZActivity) context).getreponsemessage( Globals.orderresponsemessage);
                                    database.setTransactionSuccessful();
                                    database.endTransaction();


                                } else {

                                }


                            }
                            else if(strStatus.equals("false")){
                                //resultStr = "2";
                Globals.orderresponsemessage="0";
                               // response_var="2";

                                            if (Globals.orderresponsemessage.equals("Device Not Found")) {

                                                Lite_POS_Device lite_pos_device = Lite_POS_Device.getDevice(context, "", database);
                                                lite_pos_device.setStatus("Out");
                                                long ct = lite_pos_device.updateDevice("Status=?", new String[]{"IN"}, database);
                                                if (ct > 0) {

                                                    Intent intent_category = new Intent(context, LoginActivity.class);
                                                    context.startActivity(intent_category);

                                                }


                                            }




                            }
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
                params.put("data", JsonString);
                params.put("sys_code_1", syscode1);
                params.put("sys_code_2", Globals.syscode2);
                params.put("sys_code_3",syscode3);
                params.put("sys_code_4", syscode4);
                params.put("device_code", Globals.Device_Code);
                params.put("lic_customer_license_id", liccustomerid);

                System.out.println("params" + params);

                return params;
            }


        };

        AppController.getInstance().addToRequestQueue(stringRequest);

    }*/



}
