package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by pegasus-andorid-1 on 1/16/2017.
 */

public class Order_Detail {
    private static String tableName = "order_detail";
    private String order_detail_id;
    private String device_code;
    private String order_code;
    private String reference_order_code;
    private String item_code;
    private String sr_no;
    private String cost_price;
    private String sale_price;
    private String tax;
    private String quantity;
    private String return_quantity;
    private String discount;
    private String line_total;
    private String is_combo;


    private Database db;
    private ContentValues value;

    public Order_Detail(Context context, String order_detail_id, String device_code,
                        String order_code, String reference_order_code, String item_code,
                        String sr_no, String cost_price,
                        String sale_price, String tax, String quantity,
                        String return_quantity, String discount,String line_total,String is_combo) {

        db = new Database(context);
        value = new ContentValues();

        this.set_order_detail_id(order_detail_id);
        this.set_device_code(device_code);
        this.set_order_code(order_code);
        this.set_reference_order_code(reference_order_code);
        this.set_item_code(item_code);
        this.set_sr_no(sr_no);
        this.set_cost_price(cost_price);
        this.set_sale_price(sale_price);
        this.set_tax(tax);
        this.set_quantity(quantity);
        this.set_return_quantity(return_quantity);
        this.set_discount(discount);
        this.set_line_total(line_total);
        this.set_is_combo(is_combo);
    }


    public String get_order_detail_id() {
        return order_detail_id;
    }

    public void set_order_detail_id(String order_detail_id) {
        this.order_detail_id = order_detail_id;
        value.put("order_detail_id", order_detail_id);
    }

    public String get_device_code() {
        return device_code;
    }

    public void set_device_code(String device_code) {
        this.device_code = device_code;
        value.put("device_code", device_code);
    }

    public String get_order_code() {
        return order_code;
    }

    public void set_order_code(String order_code) {
        this.order_code = order_code;
        value.put("order_code", order_code);
    }


    public String get_reference_order_code() {
        return reference_order_code;
    }

    public void set_reference_order_code(String reference_order_code) {
        this.reference_order_code = reference_order_code;
        value.put("reference_order_code", reference_order_code);
    }

    public String get_item_code() {
        return item_code;
    }

    public void set_item_code(String item_code) {
        this.item_code = item_code;
        value.put("item_code", item_code);
    }

    public String get_sr_no() {
        return sr_no;
    }

    public void set_sr_no(String sr_no) {
        this.sr_no = sr_no;
        value.put("sr_no", sr_no);
    }

    public String get_cost_price() {
        return cost_price;
    }

    public void set_cost_price(String cost_price) {
        this.cost_price = cost_price;
        value.put("cost_price", cost_price);
    }

    public String get_sale_price() {
        return sale_price;
    }

    public void set_sale_price(String sale_price) {
        this.sale_price = sale_price;
        value.put("sale_price", sale_price);
    }

    public String get_tax() {
        return tax;
    }

    public void set_tax(String tax) {
        this.tax = tax;
        value.put("tax", tax);
    }

    public String get_quantity() {
        return quantity;
    }

    public void set_quantity(String quantity) {
        this.quantity = quantity;
        value.put("quantity", quantity);
    }

    public String get_return_quantity() {
        return return_quantity;
    }

    public void set_return_quantity(String return_quantity) {
        this.return_quantity = return_quantity;
        value.put("return_quantity", return_quantity);
    }

    public String get_discount() {
        return discount;
    }

    public void set_discount(String discount) {
        this.discount = discount;
        value.put("discount", discount);
    }

    public String get_line_total() {
        return line_total;
    }

    public void set_line_total(String line_total) {
        this.line_total = line_total;
        value.put("line_total", line_total);
    }

    public String get_is_combo() {
        return is_combo;
    }

    public void set_is_combo(String is_combo) {
        this.is_combo = is_combo;
        value.put("is_combo", is_combo);
    }


    public long insertOrder_Detail(SQLiteDatabase database) {
        //SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "order_detail_id", value);
        //database.close();
        return insert;
    }

    public long updateOrder_Detail(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
       //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
       //db.close();
        return insert;
    }

    public static long delete_order_detail(Context context, String tablename, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        long delete = database.delete(tableName, whereClause, whereArgs);
//        sdb.close();
        return delete;
    }

    public static Order_Detail getOrder_Detail(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Order_Detail master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Order_Detail(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getString(13));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }

    public static Order_Detail getOrder_DetailInv(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select am.*,e.item_name, e.barcode FROM order_detail am left join item e on e.item_code = am.item_code " + WhereClasue;
        Order_Detail master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Order_Detail(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getString(13)
                       );
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return master;
    }
    // Here Changed in function  need to update all classes
    public static ArrayList<Order_Detail> getAllOrder_Detail(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Order_Detail> list = new ArrayList<Order_Detail>();
        Order_Detail master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Order_Detail(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getString(13));
                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }

    public static String getItemName(Context context, String WhereClasue)


    {
        String Query = "Select item.item_name FROM " + tableName + "  LEFT JOIN item ON item.item_code = order_detail.item_code  " + WhereClasue;

        String master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        try {
            Cursor cursor = database.rawQuery(Query, null);
            if (cursor.moveToFirst()) {
                do {
                    master = cursor.getString(0);
                } while (cursor.moveToNext());
            }
            cursor.close();
//            database.close();
//            db.close();
        } catch (Exception ex) {
        }
        return master;
    }


    public static String getItemName_L(Context context, String WhereClasue)
    {
        String Query = "Select item.description FROM " + tableName + "  LEFT JOIN item ON item.item_code = order_detail.item_code  " + WhereClasue;

        String master = null;
        Database db = new Database(context);
        SQLiteDatabase database = db.getReadableDatabase();
        try {
            Cursor cursor = database.rawQuery(Query, null);
            if (cursor.moveToFirst()) {
                do {
                    master = cursor.getString(0);
                } while (cursor.moveToNext());
            }
            cursor.close();
//            database.close();
//            db.close();
        } catch (Exception ex) {
        }
        return master;
    }

    public static String getSubtotal(Context context, String WhereClasue, SQLiteDatabase database)


    {
        String Query = "Select SUM(sale_price) as total FROM "+tableName +"" + WhereClasue;

        String master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        try {
            Cursor cursor = database.rawQuery(Query, null);
            if (cursor.moveToFirst()) {
                do {
                    master = cursor.getString(0);
                } while (cursor.moveToNext());
            }
            cursor.close();
//            database.close();
//            db.close();
        } catch (Exception ex) {
        }
        return master;
    }

}
