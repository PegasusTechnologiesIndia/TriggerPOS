package org.phomellolitepos.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.phomellolitepos.Util.Globals;

import java.util.ArrayList;

/**
 * Created by pegasus-andorid-1 on 1/19/2017.
 */

public class ShoppingCart {
    private String SRNO;
    private String Item_Code;
    private String Item_Name;
    private String Quantity;
    private String Cost_Price;
    private String Sales_Price;
    private String Tax_Price;
    private String Discount;
    private String Line_Total;

    public ShoppingCart(Context context, String SRNO, String Item_Code, String Item_Name, String Quantity,
                        String Cost_Price, String Sales_Price, String Tax_Price, String Discount, String Line_Total
    ) {

        this.set_SRNO(SRNO);
        this.set_Item_Code(Item_Code);
        this.set_Item_Name(Item_Name);
        this.set_Quantity(Quantity);
        this.set_Cost_Price(Cost_Price);
        this.set_Sales_Price(Sales_Price);
        this.set_Tax_Price(Tax_Price);
        this.set_Discount(Discount);
        this.set_Line_Total(Line_Total);


    }

    public String get_SRNO() {
        return SRNO;
    }

    public void set_SRNO(String SRNO) {
        this.SRNO = SRNO;

    }

    public String get_Line_Total() {
        return Line_Total;
    }

    public void set_Line_Total(String Line_Total) {
        this.Line_Total = Line_Total;

    }

    public String get_Discount() {
        return Discount;
    }

    public void set_Discount(String Discount) {
        this.Discount = Discount;

    }

    public String get_Tax_Price() {
        return Tax_Price;
    }

    public void set_Tax_Price(String Tax_Price) {
        this.Tax_Price = Tax_Price;

    }

    public String get_Sales_Price() {
        return Sales_Price;
    }

    public void set_Sales_Price(String Sales_Price) {
        this.Sales_Price = Sales_Price;

    }

    public String get_Item_Code() {
        return Item_Code;
    }

    public void set_Item_Code(String Item_Code) {
        this.Item_Code = Item_Code;

    }

    public String get_Item_Name() {
        return Item_Name;
    }

    public void set_Item_Name(String Item_Name) {
        this.Item_Name = Item_Name;

    }

    public String get_Quantity() {
        return Quantity;
    }

    public void set_Quantity(String Quantity) {
        this.Quantity = Quantity;

    }

    public String get_Cost_Price() {
        return Cost_Price;
    }

    public void set_Cost_Price(String Cost_Price) {
        this.Cost_Price = Cost_Price;
    }




}
