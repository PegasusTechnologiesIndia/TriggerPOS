package org.phomellolitepos.SearchOrder;

/**
 * Created by LENOVO on 2/15/2018.
 */

public class SearchOrders {

    private String Order_Code;
    private String Order_Date;
    private String Total_Amount;

    public SearchOrders(String Order_Code, String Order_Date,
                            String Total_Amount) {

        this.setOrder_Code(Order_Code);
        this.setTotal_Amount(Total_Amount);
        this.setOrder_Date(Order_Date);

    }

    public String getOrder_Code() {
        return Order_Code;
    }

    public void setOrder_Code(String order_Code) {
        Order_Code = order_Code;
    }

    public String getOrder_Date() {
        return Order_Date;
    }

    public void setOrder_Date(String order_Date) {
        Order_Date = order_Date;
    }

    public String getTotal_Amount() {
        return Total_Amount;
    }

    public void setTotal_Amount(String total_Amount) {
        Total_Amount = total_Amount;
    }
}
