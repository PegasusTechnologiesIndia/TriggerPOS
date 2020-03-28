package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Neeraj on 2/3/2017.
 */

public class Settings {

    private static String tableName = "Settings";
    private String _Id;
    private String IsOnline;
    private String printerId;
    private String printerIp;
    private String Scale;
    private String Email;
    private String Password;
    private String Logo;
    private String Manager_Email;
    private String Is_sms;
    private String Is_email;
    private String URL;
    private String Auth_Key;
    private String Sender_Id;
    private String Print_Lang;
    private String Is_Customer_Display;
    private String HSN_print;
    private String ItemTax;
    private String Copy_Right;
    private String Qty_Decimal;
    private String Footer_Text;
    private String CustomerDisplay;
    private String Is_Denomination;
    private String Is_BarcodePrint;
    private String Is_Discount;
    private String Gst_No;
    private String Print_Order;
    private String Print_Cashier;
    private String Print_InvNo;
    private String Print_InvDate;
    private String Print_DeviceID;
    private String Is_KOT_Print;
    private String Is_Print_Invoice;
    private String Is_File_Share;
    private String Host;
    private String Port;
    private String Change_Parameter;
    private String Is_Accounts;
    private String Is_Stock_Manager;
    private String Home_Layout;
    private String Is_Cash_Drawer;
    private String Is_Zero_Stock;
    private String Is_Change_Price;
    private String No_Of_Print;
    private String GST_Label;
    private String Is_ZDetail_InPrint;
    private String Is_Device_Customer_Show;
    private String Is_Print_Dialog_Show;
    private String Is_BR_Scanner_Show;
    private String Default_Ordertype;
    private String Print_Memo;
    private String Is_Cost_Show;
    private String QR_Type;


    private Database db;
    private ContentValues value;

    public Settings(Context context, String _Id, String IsOnline, String printerId, String printerIp, String Scale, String Email, String Password, String Logo, String Manager_Email,
                    String Is_sms,String Is_email,String URL,String Auth_Key,
                    String Sender_Id,String Print_Lang,String Is_Customer_Display,
                    String HSN_print,String ItemTax,String Copy_Right,String Qty_Decimal,
                    String Footer_Text,String CustomerDisplay,
                    String Is_Denomination,String Is_BarcodePrint,String Is_Discount,String Gst_No,String Print_Order,
                    String Print_Cashier,String Print_InvNo,String Print_InvDate,String Print_DeviceID,
                    String Is_KOT_Print,String Is_Print_Invoice,String Is_File_Share,String Host,
                    String Port,String Change_Parameter,String Is_Accounts,
                    String Is_Stock_Manager,String Home_Layout,
                    String Is_Cash_Drawer,String Is_Zero_Stock,
                    String Is_Change_Price,String No_Of_Print,
                    String GST_Label,String Is_ZDetail_InPrint,
                    String Is_Device_Customer_Show,
                    String Is_Print_Dialog_Show,
                    String Is_BR_Scanner_Show,String Default_Ordertype
                    ,String Print_Memo,String Is_Cost_Show,String QR_Type) {

        db = new Database(context);
        value = new ContentValues();

        this.set_Id(_Id);
        this.set_IsOnline(IsOnline);
        this.setPrinterId(printerId);
        this.setPrinterIp(printerIp);
        this.set_Scale(Scale);
        this.set_Email(Email);
        this.set_Password(Password);
        this.set_Logo(Logo);
        this.set_Manager_Email(Manager_Email);
        this.set_Is_sms(Is_sms);
        this.set_Is_email(Is_email);
        this.set_URL(URL);
        this.set_Auth_Key(Auth_Key);
        this.set_Sender_Id(Sender_Id);
        this.set_Print_Lang(Print_Lang);
        this.set_Is_Customer_Display(Is_Customer_Display);
        this.set_HSN_print(HSN_print);
        this.set_ItemTax(ItemTax);
        this.set_Copy_Right(Copy_Right);
        this.set_Qty_Decimal(Qty_Decimal);
        this.set_Footer_Text(Footer_Text);
        this.set_CustomerDisplay(CustomerDisplay);
        this.set_Is_Denomination(Is_Denomination);
        this.set_Is_BarcodePrint(Is_BarcodePrint);
        this.set_Is_Discount(Is_Discount);
        this.set_Gst_No(Gst_No);
        this.set_Print_Order(Print_Order);
        this.set_Print_Cashier(Print_Cashier);
        this.set_Print_InvNo(Print_InvNo);
        this.set_Print_InvDate(Print_InvDate);
        this.set_Print_DeviceID(Print_DeviceID);
        this.set_Is_KOT_Print(Is_KOT_Print);
        this.set_Is_Print_Invoice(Is_Print_Invoice);
        this.set_Is_File_Share(Is_File_Share);
        this.set_Host(Host);
        this.set_Port(Port);
        this.set_Change_Parameter(Change_Parameter);
        this.set_Is_Accounts(Is_Accounts);
        this.set_Is_Stock_Manager(Is_Stock_Manager);
        this.set_Home_Layout(Home_Layout);
        this.set_Is_Cash_Drawer(Is_Cash_Drawer);
        this.set_Is_Zero_Stock(Is_Zero_Stock);
        this.set_Is_Zero_Stock(Is_Zero_Stock);
        this.set_Is_Change_Price(Is_Change_Price);
        this.set_No_Of_Print(No_Of_Print);
        this.set_GST_Label(GST_Label);
        this.set_Is_ZDetail_InPrint(Is_ZDetail_InPrint);
        this.set_Is_Device_Customer_Show(Is_Device_Customer_Show);
        this.set_Is_Print_Dialog_Show(Is_Print_Dialog_Show);
        this.set_Is_BR_Scanner_Show(Is_BR_Scanner_Show);
        this.set_Default_Ordertype(Default_Ordertype);
        this.set_Print_Memo(Print_Memo);
        this.set_Is_Cost_Show(Is_Cost_Show);
        this.set_QR_Type(QR_Type);
    }

    public String get_QR_Type() {
        return QR_Type;
    }

    public void set_QR_Type(String QR_Type) {
        this.QR_Type = QR_Type;
        value.put("QR_Type", QR_Type);
    }


    public String getPrinterIp() {
        return printerIp;
    }

    public void setPrinterIp(String printerIp) {
        this.printerIp = printerIp;
        value.put("printerIp", printerIp);
    }

    public String getPrinterId() {
        return printerId;
    }

    public void setPrinterId(String printerId) {
        this.printerId = printerId;
        value.put("printerId", printerId);
    }

    public String get_Id() {
        return _Id;
    }

    public void set_Id(String _Id) {
        this._Id = _Id;
        value.put("_Id", _Id);
    }

    public String get_IsOnline() {
        return IsOnline;
    }

    public void set_IsOnline(String IsOnline) {
        this.IsOnline = IsOnline;
        value.put("IsOnline", IsOnline);
    }

    public String get_Scale() {
        return Scale;
    }

    public void set_Scale(String Scale) {
        this.Scale = Scale;
        value.put("Scale", Scale);
    }

    public String get_Email() {
        return Email;
    }

    public void set_Email(String Email) {
        this.Email = Email;
        value.put("Email", Email);
    }

    public String get_Password() {
        return Password;
    }

    public void set_Password(String Password) {
        this.Password = Password;
        value.put("Password", Password);
    }


    public String get_Logo() {
        return Logo;
    }

    public void set_Logo(String Logo) {
        this.Logo = Logo;
        value.put("Logo", Logo);
    }

    public String get_Is_sms() {
        return Is_sms;
    }

    public void set_Is_sms(String Is_sms) {
        this.Is_sms = Is_sms;
        value.put("Is_sms", Is_sms);
    }


    public String get_Is_email() {
        return Is_email;
    }

    public void set_Is_email(String Is_email) {
        this.Is_email = Is_email;
        value.put("Is_email", Is_email);
    }


    public String get_Manager_Email() {
        return Manager_Email;
    }

    public void set_Manager_Email(String Manager_Email) {
        this.Manager_Email = Manager_Email;
        value.put("Manager_Email", Manager_Email);
    }


    public String get_URL() {
        return URL;
    }

    public void set_URL(String URL) {
        this.URL = URL;
        value.put("URL", URL);
    }

    public String get_Auth_Key() {
        return Auth_Key;
    }

    public void set_Auth_Key(String Auth_Key) {
        this.Auth_Key = Auth_Key;
        value.put("Auth_Key", Auth_Key);
    }


    public String get_Sender_Id() {
        return Sender_Id;
    }

    public void set_Sender_Id(String Sender_Id) {
        this.Sender_Id = Sender_Id;
        value.put("Sender_Id", Sender_Id);
    }

    public String get_Print_Lang() {
        return Print_Lang;
    }

    public void set_Print_Lang(String Print_Lang) {
        this.Print_Lang = Print_Lang;
        value.put("Print_Lang", Print_Lang);
    }

    public String get_Is_Customer_Display() {
        return Is_Customer_Display;
    }

    public void set_Is_Customer_Display(String Is_Customer_Display) {
        this.Is_Customer_Display = Is_Customer_Display;
        value.put("Is_Customer_Display", Is_Customer_Display);
    }

    public String get_HSN_print() {
        return HSN_print;
    }

    public void set_HSN_print(String HSN_print) {
        this.HSN_print = HSN_print;
        value.put("HSN_print", HSN_print);
    }

    public String get_ItemTax() {
        return ItemTax;
    }

    public void set_ItemTax(String ItemTax) {
        this.ItemTax = ItemTax;
        value.put("ItemTax", ItemTax);
    }

    public String get_Copy_Right() {
        return Copy_Right;
    }

    public void set_Copy_Right(String Copy_Right) {
        this.Copy_Right = Copy_Right;
        value.put("Copy_Right", Copy_Right);
    }

    public String get_Qty_Decimal() {
        return Qty_Decimal;
    }

    public void set_Qty_Decimal(String Qty_Decimal) {
        this.Qty_Decimal = Qty_Decimal;
        value.put("Qty_Decimal", Qty_Decimal);
    }

    public String get_Footer_Text() {
        return Footer_Text;
    }

    public void set_Footer_Text(String Footer_Text) {
        this.Footer_Text = Footer_Text;
        value.put("Footer_Text", Footer_Text);
    }

    public String get_CustomerDisplay() {
        return CustomerDisplay;
    }

    public void set_CustomerDisplay(String CustomerDisplay) {
        this.CustomerDisplay = CustomerDisplay;
        value.put("CustomerDisplay", CustomerDisplay);
    }

    public String get_Is_Denomination() {
        return Is_Denomination;
    }

    public void set_Is_Denomination(String Is_Denomination) {
        this.Is_Denomination = Is_Denomination;
        value.put("Is_Denomination", Is_Denomination);
    }

    public String get_Is_BarcodePrint() {
        return Is_BarcodePrint;
    }

    public void set_Is_BarcodePrint(String Is_BarcodePrint) {
        this.Is_BarcodePrint = Is_BarcodePrint;
        value.put("Is_BarcodePrint", Is_BarcodePrint);
    }

    public String get_Is_Discount() {
        return Is_Discount;
    }

    public void set_Is_Discount(String Is_Discount) {
        this.Is_Discount = Is_Discount;
        value.put("Is_Discount", Is_Discount);
    }

    public String get_Gst_No() {
        return Gst_No;
    }

    public void set_Gst_No(String Gst_No) {
        this.Gst_No = Gst_No;
        value.put("Gst_No", Gst_No);
    }

    public String get_Print_Order() {
        return Print_Order;
    }

    public void set_Print_Order(String Print_Order) {
        this.Print_Order = Print_Order;
        value.put("Print_Order", Print_Order);
    }

    public String get_Print_Cashier() {
        return Print_Cashier;
    }

    public void set_Print_Cashier(String Print_Cashier) {
        this.Print_Cashier = Print_Cashier;
        value.put("Print_Cashier", Print_Cashier);
    }

    public String get_Print_InvNo() {
        return Print_InvNo;
    }

    public void set_Print_InvNo(String Print_InvNo) {
        this.Print_InvNo = Print_InvNo;
        value.put("Print_InvNo", Print_InvNo);
    }

    public String get_Print_InvDate() {
        return Print_InvDate;
    }

    public void set_Print_InvDate(String Print_InvDate) {
        this.Print_InvDate = Print_InvDate;
        value.put("Print_InvDate", Print_InvDate);
    }


    public String get_Print_DeviceID() {
        return Print_DeviceID;
    }

    public void set_Print_DeviceID(String Print_DeviceID) {
        this.Print_DeviceID = Print_DeviceID;
        value.put("Print_DeviceID", Print_DeviceID);
    }

    public String get_Is_KOT_Print() {
        return Is_KOT_Print;
    }

    public void set_Is_KOT_Print(String Is_KOT_Print) {
        this.Is_KOT_Print = Is_KOT_Print;
        value.put("Is_KOT_Print", Is_KOT_Print);
    }

    public String get_Is_Print_Invoice() {
        return Is_Print_Invoice;
    }

    public void set_Is_Print_Invoice(String Is_Print_Invoice) {
        this.Is_Print_Invoice = Is_Print_Invoice;
        value.put("Is_Print_Invoice", Is_Print_Invoice);
    }

    public String get_Is_File_Share() {
        return Is_File_Share;
    }

    public void set_Is_File_Share(String Is_File_Share) {
        this.Is_File_Share = Is_File_Share;
        value.put("Is_File_Share", Is_File_Share);
    }

    public String get_Port() {
        return Port;
    }

    public void set_Port(String Port) {
        this.Port = Port;
        value.put("Port", Port);
    }

    public String get_Host() {
        return Host;
    }

    public void set_Host(String Host) {
        this.Host = Host;
        value.put("Host", Host);
    }

    public String get_Change_Parameter() {
        return Change_Parameter;
    }

    public void set_Change_Parameter(String Change_Parameter) {
        this.Change_Parameter = Change_Parameter;
        value.put("Change_Parameter", Change_Parameter);
    }

    public String get_Is_Accounts() {
        return Is_Accounts;
    }

    public void set_Is_Accounts(String Is_Accounts) {
        this.Is_Accounts = Is_Accounts;
        value.put("Is_Accounts", Is_Accounts);
    }

    public String get_Is_Stock_Manager() {
        return Is_Stock_Manager;
    }

    public void set_Is_Stock_Manager(String Is_Stock_Manager) {
        this.Is_Stock_Manager = Is_Stock_Manager;
        value.put("Is_Stock_Manager", Is_Stock_Manager);
    }

    public String get_Home_Layout() {
        return Home_Layout;
    }

    public void set_Home_Layout(String Home_Layout) {
        this.Home_Layout = Home_Layout;
        value.put("Home_Layout", Home_Layout);
    }

    public String get_Is_Cash_Drawer() {
        return Is_Cash_Drawer;
    }

    public void set_Is_Cash_Drawer(String Is_Cash_Drawer) {
        this.Is_Cash_Drawer = Is_Cash_Drawer;
        value.put("Is_Cash_Drawer", Is_Cash_Drawer);
    }

    public String get_Is_Zero_Stock() {
        return Is_Zero_Stock;
    }

    public void set_Is_Zero_Stock(String Is_Zero_Stock) {
        this.Is_Zero_Stock = Is_Zero_Stock;
        value.put("Is_Zero_Stock", Is_Zero_Stock);
    }

    public String get_Is_Change_Price() {
        return Is_Change_Price;
    }

    public void set_Is_Change_Price(String Is_Change_Price) {
        this.Is_Change_Price = Is_Change_Price;
        value.put("Is_Change_Price", Is_Change_Price);
    }

    public String get_No_Of_Print() {
        return No_Of_Print;
    }

    public void set_No_Of_Print(String No_Of_Print) {
        this.No_Of_Print = No_Of_Print;
        value.put("No_Of_Print", No_Of_Print);
    }

    public String get_GST_Label() {
        return GST_Label;
    }

    public void set_GST_Label(String GST_Label) {
        this.GST_Label = GST_Label;
        value.put("GST_Label", GST_Label);
    }

    public String get_Is_ZDetail_InPrint() {
        return Is_ZDetail_InPrint;
    }

    public void set_Is_ZDetail_InPrint(String Is_ZDetail_InPrint) {
        this.Is_ZDetail_InPrint = Is_ZDetail_InPrint;
        value.put("Is_ZDetail_InPrint", Is_ZDetail_InPrint);
    }

    public String get_Is_Device_Customer_Show() {
        return Is_Device_Customer_Show;
    }

    public void set_Is_Device_Customer_Show(String Is_Device_Customer_Show) {
        this.Is_Device_Customer_Show = Is_Device_Customer_Show;
        value.put("Is_Device_Customer_Show", Is_Device_Customer_Show);
    }

    public String get_Is_Print_Dialog_Show() {
        return Is_Print_Dialog_Show;
    }

    public void set_Is_Print_Dialog_Show(String Is_Print_Dialog_Show) {
        this.Is_Print_Dialog_Show = Is_Print_Dialog_Show;
        value.put("Is_Print_Dialog_Show", Is_Print_Dialog_Show);
    }

    public String get_Is_BR_Scanner_Show() {
        return Is_BR_Scanner_Show;
    }

    public void set_Is_BR_Scanner_Show(String Is_BR_Scanner_Show) {
        this.Is_BR_Scanner_Show = Is_BR_Scanner_Show;
        value.put("Is_BR_Scanner_Show", Is_BR_Scanner_Show);
    }

    public String get_Default_Ordertype() {
        return Default_Ordertype;
    }

    public void set_Default_Ordertype(String Default_Ordertype) {
        this.Default_Ordertype = Default_Ordertype;
        value.put("Default_Ordertype", Default_Ordertype);
    }

    public String get_Print_Memo() {
        return Print_Memo;
    }

    public void set_Print_Memo(String Print_Memo) {
        this.Print_Memo = Print_Memo;
        value.put("Print_Memo", Print_Memo);
    }

    public String get_Is_Cost_Show() {
        return Is_Cost_Show;
    }

    public void set_Is_Cost_Show(String Is_Cost_Show) {
        this.Is_Cost_Show = Is_Cost_Show;
        value.put("Is_Cost_Show", Is_Cost_Show);
    }


    public long insertSettings(SQLiteDatabase database) {
        //SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "_Id", value);
        //database.close();
        return insert;
    }

    public long updateSettings(String whereClause, String[] whereArgs, SQLiteDatabase database) throws SQLiteConstraintException {
//        SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause, whereArgs, SQLiteDatabase.CONFLICT_FAIL);
//        sdb.close();
        return insert;
    }

    public static long delete_settings(Context context, String tablename, SQLiteDatabase database, String whereClause, String[] whereArgs) {
        // Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        long delete = database.delete(tableName, whereClause, whereArgs);
//        sdb.close();
        return delete;
    }

    public static Settings getSettings(Context context, SQLiteDatabase database, String WhereClasue) {
        String Query = "Select  *  FROM " + tableName + " " + WhereClasue;
        Settings master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        try {
            Cursor cursor = database.rawQuery(Query, null);
            if (cursor.moveToFirst()) {
                do {
                    master = new Settings(context, cursor.getString(0), cursor.getString(1),
                            cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5),
                            cursor.getString(6), cursor.getString(7), cursor.getString(8),
                            cursor.getString(9), cursor.getString(10), cursor.getString(11),
                            cursor.getString(12), cursor.getString(13), cursor.getString(14), cursor.getString(15),
                            cursor.getString(16), cursor.getString(17), cursor.getString(18), cursor.getString(19),
                            cursor.getString(20), cursor.getString(21), cursor.getString(22), cursor.getString(23),
                            cursor.getString(24), cursor.getString(25), cursor.getString(26), cursor.getString(27),
                            cursor.getString(28), cursor.getString(29), cursor.getString(30), cursor.getString(31)
                            ,cursor.getString(32), cursor.getString(33),cursor.getString(34),
                            cursor.getString(35), cursor.getString(36),cursor.getString(37),
                            cursor.getString(38),cursor.getString(39)
                            ,cursor.getString(40),cursor.getString(41)
                            ,cursor.getString(42),cursor.getString(43)
                            ,cursor.getString(44),cursor.getString(45),
                            cursor.getString(46),cursor.getString(47)
                            ,cursor.getString(48),cursor.getString(49)
                            ,cursor.getString(50),cursor.getString(51),cursor.getString(52));
                } while (cursor.moveToNext());
            }
            cursor.close();


        } catch (Exception ex) {
            String ab = ex.getMessage();
            ab=ab;
        }
        return master;
    }


    public static ArrayList<Settings> getAllSettings(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        ArrayList<Settings> list = new ArrayList<Settings>();
        Settings master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.moveToFirst()) {
            do {
                master = new Settings(context, cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10), cursor.getString(11),
                        cursor.getString(12), cursor.getString(13),
                        cursor.getString(14), cursor.getString(15),
                        cursor.getString(16), cursor.getString(17),
                        cursor.getString(18),cursor.getString(19),
                        cursor.getString(20),cursor.getString(21), cursor.getString(22), cursor.getString(23),
                        cursor.getString(24), cursor.getString(25), cursor.getString(26),cursor.getString(27),
                        cursor.getString(28),cursor.getString(29),cursor.getString(30),cursor.getString(31)
                        ,cursor.getString(32),cursor.getString(33),cursor.getString(34), cursor.getString(35)
                        ,cursor.getString(36),cursor.getString(37), cursor.getString(38)
                        ,cursor.getString(39),cursor.getString(40)
                        ,cursor.getString(41),cursor.getString(42),cursor.getString(43)
                        ,cursor.getString(44),cursor.getString(45)
                        ,cursor.getString(46),cursor.getString(47)
                        ,cursor.getString(48),cursor.getString(49)
                        ,cursor.getString(50),cursor.getString(51),cursor.getString(52));
                list.add(master);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        database.close();
//        db.close();
        return list;
    }

}
