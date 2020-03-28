package org.phomellolitepos.Util;

import android.content.Context;

public class Loyalty_Redeem {

    private String id;
    private String redeem_point;
    private String redeem_value;

    public Loyalty_Redeem(Context context, String id, String redeem_point, String redeem_value) {

        this.setId(id);
        this.setRedeem_point(redeem_point);
        this.setRedeem_value(redeem_value);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRedeem_point() {
        return redeem_point;
    }

    public void setRedeem_point(String redeem_point) {
        this.redeem_point = redeem_point;
    }

    public String getRedeem_value() {
        return redeem_value;
    }

    public void setRedeem_value(String redeem_value) {
        this.redeem_value = redeem_value;
    }
}
