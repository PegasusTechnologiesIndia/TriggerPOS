package org.phomellolitepos.Util;

import android.content.Context;

public class ReturnItemDetail {

    private String item_code;
    private String quantity;
    private String return_qty;
    private String tax;
    private String price;
    private String discount;
    private String line_total;


    public ReturnItemDetail(Context context, String item_code, String quantity, String return_qty, String tax, String price, String discount, String line_total) {

        this.setItem_code(item_code);
        this.setQuantity(quantity);
        this.setReturn_qty(return_qty);
        this.setTax(tax);
        this.setPrice(price);
        this.setDiscount(discount);
        this.setLine_total(line_total);
    }

    public String getItem_code() {
        return item_code;
    }

    public void setItem_code(String item_code) {
        this.item_code = item_code;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getReturn_qty() {
        return return_qty;
    }

    public void setReturn_qty(String return_qty) {
        this.return_qty = return_qty;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getLine_total() {
        return line_total;
    }

    public void setLine_total(String line_total) {
        this.line_total = line_total;
    }
}
