package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;

public class Return_Item_Tax {
    private static String tableName = "return_detail_tax";
    private String order_return_detail_tax_id;
    private String order_return_voucher_no;
    private String sr_no;
    private String item_code;
    private String tax_id;
    private String tax_type;
    private String rate;
    private String tax_value;

    public Return_Item_Tax(Context context, String order_returndetail_tax_id, String order_returnvoucherno, String sr_no, String item_code, String tax_id, String tax_type,
                             String rate, String tax_value) {


        this.setOrder_return_detail_tax_id(order_returndetail_tax_id);
        this.setOrder_return_voucher_no(order_returnvoucherno);
        this.setSr_no(sr_no);
        this.setItem_code(item_code);
        this.setTax_id(tax_id);
        this.setTax_type(tax_type);
        this.setRate(rate);
        this.setTax_value(tax_value);



    }

    public String getOrder_return_detail_tax_id() {
        return order_return_detail_tax_id;
    }

    public void setOrder_return_detail_tax_id(String order_return_detail_tax_id) {
        this.order_return_detail_tax_id = order_return_detail_tax_id;
    }

    public String getOrder_return_voucher_no() {
        return order_return_voucher_no;
    }

    public void setOrder_return_voucher_no(String order_return_voucher_no) {
        this.order_return_voucher_no = order_return_voucher_no;
    }

    public String getSr_no() {
        return sr_no;
    }

    public void setSr_no(String sr_no) {
        this.sr_no = sr_no;
    }

    public String getItem_code() {
        return item_code;
    }

    public void setItem_code(String item_code) {
        this.item_code = item_code;
    }

    public String getTax_id() {
        return tax_id;
    }

    public void setTax_id(String tax_id) {
        this.tax_id = tax_id;
    }

    public String getTax_type() {
        return tax_type;
    }

    public void setTax_type(String tax_type) {
        this.tax_type = tax_type;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getTax_value() {
        return tax_value;
    }

    public void setTax_value(String tax_value) {
        this.tax_value = tax_value;
    }
}
