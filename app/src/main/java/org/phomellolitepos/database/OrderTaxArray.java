package org.phomellolitepos.database;

import android.content.Context;

/**
 * Created by Neeraj on 4/17/2017.
 */

public class OrderTaxArray {

    private String order_tax_id;
    private String order_code;
    private String tax_id;
    private String tax_type;
    private String rate;
    private String tax_value;

    public OrderTaxArray(Context context, String order_tax_id, String order_code, String tax_id, String tax_type,
                         String rate, String tax_value) {

        this.set_order_tax_id(order_tax_id);
        this.set_order_code(order_code);
        this.set_tax_id(tax_id);
        this.set_tax_type(tax_type);
        this.set_rate(rate);
        this.set_value(tax_value);

    }


    public String get_order_tax_id() {
        return order_tax_id;
    }

    public void set_order_tax_id(String order_tax_id) {
        this.order_tax_id = order_tax_id;

    }


    public String get_order_code() {
        return order_code;
    }

    public void set_order_code(String order_code) {
        this.order_code = order_code;

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
