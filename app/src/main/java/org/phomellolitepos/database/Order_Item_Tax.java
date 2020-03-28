package org.phomellolitepos.database;

import android.content.Context;

/**
 * Created by Neeraj on 4/17/2017.
 */

public class Order_Item_Tax {

    private static String tableName = "order_detail_tax";
    private String order_detail_tax_id;
    private String order_code;
    private String sr_no;
    private String item_code;
    private String tax_id;
    private String tax_type;
    private String rate;
    private String tax_value;

    public Order_Item_Tax(Context context, String order_detail_tax_id, String order_code, String sr_no, String item_code, String tax_id, String tax_type,
                            String rate, String tax_value) {

        this.set_order_detail_tax_id(order_detail_tax_id);
        this.set_order_code(order_code);
        this.set_sr_no(sr_no);
        this.set_item_code(item_code);
        this.set_tax_id(tax_id);
        this.set_tax_type(tax_type);
        this.set_rate(rate);
        this.set_value(tax_value);


    }


    public String get_order_detail_tax_id() {
        return order_detail_tax_id;
    }

    public void set_order_detail_tax_id(String order_detail_tax_id) {
        this.order_detail_tax_id = order_detail_tax_id;

    }


    public String get_order_code() {
        return order_code;
    }

    public void set_order_code(String order_code) {
        this.order_code = order_code;

    }

    public String get_sr_no() {
        return sr_no;
    }

    public void set_sr_no(String sr_no) {
        this.sr_no = sr_no;

    }

    public String get_item_code() {
        return item_code;
    }

    public void set_item_code(String item_code) {
        this.item_code = item_code;

    }

    public String get_tax_id() {
        return tax_id;
    }

    public void set_tax_id(String tax_id) {
        this.tax_id = tax_id;

    }

    public String get_tax_type() {
        return tax_type;
    }

    public void set_tax_type(String tax_type) {
        this.tax_type = tax_type;

    }

    public String get_rate() {
        return rate;
    }

    public void set_rate(String rate) {
        this.rate = rate;

    }

    public String get_value() {
        return tax_value;
    }

    public void set_value(String tax_value) {
        this.tax_value = tax_value;

    }



}
