package org.phomellolitepos.database;

import android.content.Context;

/**
 * Created by LENOVO on 10/27/2017.
 */

public class SplitPaymentList {

    private String Payment_Type;
    private String Payment_Name;
    private String Amount;


    public SplitPaymentList(Context context, String Payment_Type,String Payment_Name,String Amount) {

        this.setPayment_Type(Payment_Type);
        this.setPayment_Name(Payment_Name);
        this.setAmount(Amount);

    }

    public String getPayment_Type() {
        return Payment_Type;
    }

    public void setPayment_Type(String payment_Type) {
        Payment_Type = payment_Type;
    }

    public String getPayment_Name() {
        return Payment_Name;
    }

    public void setPayment_Name(String payment_Name) {
        Payment_Name = payment_Name;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }
}
