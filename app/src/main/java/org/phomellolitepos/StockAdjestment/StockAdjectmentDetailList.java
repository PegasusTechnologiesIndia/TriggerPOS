package org.phomellolitepos.StockAdjestment;

import android.content.Context;

/**
 * Created by LENOVO on 4/4/2018.
 */

public class StockAdjectmentDetailList {
    private String id;
    private String ref_voucher_no;
    private String s_no;
    private String item_code;
    private String qty;
    private String in_out_flag;
    private String item_name;
    private String price;
    private String line_total;

    public StockAdjectmentDetailList(Context context, String id, String ref_voucher_no, String s_no, String item_code,
                        String qty, String in_out_flag,String item_name,String price,String line_total) {

        this.setId(id);
        this.setRef_voucher_no(ref_voucher_no);
        this.setS_no(s_no);
        this.setItem_code(item_code);
        this.setQty(qty);
        this.setIn_out_flag(in_out_flag);
        this.setItem_name(item_name);
        this.setPrice(price);
        this.setLine_total(line_total);


    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRef_voucher_no() {
        return ref_voucher_no;
    }

    public void setRef_voucher_no(String ref_voucher_no) {
        this.ref_voucher_no = ref_voucher_no;
    }

    public String getS_no() {
        return s_no;
    }

    public void setS_no(String s_no) {
        this.s_no = s_no;
    }

    public String getItem_code() {
        return item_code;
    }

    public void setItem_code(String item_code) {
        this.item_code = item_code;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getIn_out_flag() {
        return in_out_flag;
    }

    public void setIn_out_flag(String in_out_flag) {
        this.in_out_flag = in_out_flag;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLine_total() {
        return line_total;
    }

    public void setLine_total(String line_total) {
        this.line_total = line_total;
    }
}
