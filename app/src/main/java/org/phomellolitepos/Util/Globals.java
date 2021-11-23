package org.phomellolitepos.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.inputmethodservice.Keyboard;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import org.phomellolitepos.Adapter.Vehicle_Order;
import org.phomellolitepos.AppController;
import org.phomellolitepos.StockAdjestment.StockAdjectmentDetailList;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Lite_POS_Device;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.OrderTaxArray;
import org.phomellolitepos.database.Order_Item_Tax;
import org.phomellolitepos.database.Order_Type_Tax;
import org.phomellolitepos.database.Return_Item_Tax;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.ShoppingCart;
import org.phomellolitepos.database.SplitPaymentList;
import org.phomellolitepos.database.Tax_Master;
import org.phomellolitepos.database.VoidShoppingCart;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import au.com.bytecode.opencsv.CSVWriter;
import jxl.Cell;
import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class Globals {
    public static String ListLimit = "100";
    public static double DiscountPer = 0;
    public static String Param1 = "";
    public static String Param2 = "";
    public static double Net_Amount = 0;
    public static double Inv_Discount = 0;
    public static double discountPer=0;
    public static double Inv_Tax = 0;
    public static double Sub_Total = 0;
    public static double CouponTotal = 0;
    public static String Inv_Description = "";
    public static String Inv_Opr = "";
    public static String Inv_Odr_Code = "";
    public static String Inv_Delivery_Date = "";
    public static String CardNo = "";
    public static String ZoneID = "";
    public static String table_code = "";
    public static String table_name="";
    public static String strorderType = "";
    // StockAdjustmentDetail List Variable declaration
    public static ArrayList<StockAdjectmentDetailList> data =new ArrayList<StockAdjectmentDetailList>();
    //.........................................
    public static String TicketCategory = "";
    public static String rtick = "";
    public static String Depart = "";
    public static String Arriv = "";
    public static String From = "";
    public static String To = "";
    public static String TickCat = "";
    public static String TicBrand = "";
    public static String TicDay = "";
    public static String TicDate = "";
    public static int pageNO = 0;
    public static String strOldCrAmt = "0";
    public static String ErrorMsg = "";
    public static String UserCode = "";
    public static String BarcodeReslt = "";
    public static String Version_Name = "";
    public static int Version_Code;
    public static String CheckContact = "0";
    public static String Industry_Type = "2";
    public static String ModeResrv = "";
    public static String UserResrv = "";
    public static String CustomerResrv = "";
    public static int OrientValue;
    public static String ReportCondition = "";
    public static String strReturn_Contact_Name="";
    public static boolean OrintFlagP = false;
    public static boolean OrintFlagL = false;
    public static String AccountBarcode = "0";
    public static String withItem = "1";
    public static String str_date="";
    public static String onInvoice = "0";
    public static String spinner_code_value = "";
    static  Database db = new Database(AppController.getInstance());
    static  SQLiteDatabase  database = db.getWritableDatabase();
    public static Settings objsettings = Settings.getSettings(AppController.getInstance(),Globals.database,"");
    public static ArrayList<Vehicle_Order> vehicleorderList = new ArrayList<Vehicle_Order>();
    public static ArrayList<Vehicle_Order> newvehicleorderList = new ArrayList<Vehicle_Order>();
    public static ArrayList<Loyalty_Redeem> LoyaltyRedeem = new ArrayList<Loyalty_Redeem>();
    public static ArrayList<SplitPaymentList> splitPsyMd = new ArrayList<SplitPaymentList>();
    public static ArrayList<ShoppingCart> cart = new ArrayList<ShoppingCart>();
    public static ArrayList<VoidShoppingCart> voidcart = new ArrayList<VoidShoppingCart>();
    public static ArrayList<OrderTaxArray> order_tax_array = new ArrayList<OrderTaxArray>();
    public static ArrayList<Order_Item_Tax> order_item_tax = new ArrayList<Order_Item_Tax>();
    public static ArrayList<Return_Item_Tax> return_item_tax = new ArrayList<Return_Item_Tax>();
    public static ArrayList<String> CMD_Images = new ArrayList<String>();
    public static ArrayList<String> arrayListGetFile_Image=new ArrayList<>();

    //Server


   public static String url =Globals.objsettings.getApi_Ip();
  public static String App_IP_URL = "http://"+Globals.url+"/trigger-pos/index.php/api/";
 //public static String App_IP_URL = "http://192.168.29.243:85/trigger-pos/index.php/api/"; // Local Ip

   //Local
// public static String App_IP_URL = "http://192.168.29.201/trigger-pos-ar/index.php/api/";
   // public static String App_IP = "192.168.2.72";
   // public static String imageURL ="http://www.pegasustek.com/trigger-pos/upload/demo_files/images/india/";
   public static String imageURL ="http://"+Globals.url+"/trigger-pos/upload/demo_files/images/india/";

 //   public static String App_IP_URL = "http://74.208.235.72:85/trigger-pos-ar/index.php/api/"; // Server Ip
    public static Cursor online_report_cursor = null;
    public static String Company_Id;
    public static int SRNO = 1;
    public static String folder = "mnt/sdcard/TriggerPOS/";
    public static String pdffolder = "PDF Report";
    public static String item_category_code = "";
    public static String Watermark = "Trigger POS DEMO";
    public static String user = "";
    public static String username = "";
    public static String userId = "";

    public static String devicename="";
    public static String strOrder_type_id = "1";
    public static String strContact_Code = "";
    public static String localstrContact_Code = "";
    public static String strResvContact_Code = "";
    public static String strContact_Name = "";
    public static String stritem_name = "";
    public static String strIsBlueService = "";
    public static Bitmap logo1 = null;
    public static String logo = null;
    public static String strTable_Code = "";
    public static String strZoneName = "";
    public static String Order_Code = "";
    public static String Operation = "";
    public static String load_form_cart = "0";
    public static String strIsBarcodePrint = "";
    public static String strIsDenominationPrint = "";
    public static String strIsDiscountPrint = "";
    public static String GSTNo = "";
    public static String GSTLbl = "";
    public static String PrintOrder = "";
    public static String PrintCashier = "";
    public static String PrintInvNo = "";
    public static String PrintInvDate = "";
    public static String PrintDeviceID = "";
    public static String seedValue = "PEGASUS";
    public static int printer;
    public static double NetAmount = 0;
    public static double InvoiceDiscount = 0;
    public static double InvoiceTax = 0;
    public static boolean cameraFlag = false;
    public static String reg_code = "";
    public static String projectid = "";
    public static String locname="";
    public static String App_Lic_Base_URL="http://www.pegasustech.net";
    public static String isuse="1";
    public static String master_product_id="670";
    public static String lic_customer_license_id="0";
    public static String license_id="";
    public static String isuse_logout="0";
    public static String serialno="";
    public static String androidid="";
    public static String mykey="";
    public static String syscode2="5";
    public static String responsemessage="";
    public static String orderresponsemessage="";
    public static String strvoucherno="";
    public static boolean gpsFlag = true;
    public static String latitude="0.00";
    public static String longitude="0.00";
    public static String locationddress="";
    public static String str_userpassword;
    public static String localelang ;
    public static String NoTax="" ;  // Non-Taxable Customer
    public static String Taxwith_state="" ; // Taxable Customer
    public static String Taxdifferent_state="" ; // Taxable with Different State

    public static JSONObject jsonArray_background= new JSONObject();;
    public static void  setEmpty() {
        CouponTotal = 0;
        Globals.Param1 = "";

        Globals.Param2 = "";
        Globals.strContact_Name = "";
        Globals.strContact_Code = "";
        Globals.strResvContact_Code = "";
        splitPsyMd = new ArrayList<SplitPaymentList>();
        cart = new ArrayList<ShoppingCart>();
        voidcart=new ArrayList<VoidShoppingCart>();
        LoyaltyRedeem = new ArrayList<Loyalty_Redeem>();
        NetAmount = 0;
        SRNO = 1;

        InvoiceDiscount = 0;
        InvoiceTax = 0;
        TotalItemPrice = 0;
        TotalItemCost = 0;
        TotalItem = 0;
        TotalQty = 0;
        strTable_Code = "";
        load_form_cart = "0";
        strContact_Name = "";
        NoTax="";
        Taxwith_state="";
        Taxdifferent_state="";
        order_item_tax = new ArrayList<Order_Item_Tax>();
        order_tax_array = new ArrayList<OrderTaxArray>();
        strOrder_type_id = "1";
        Net_Amount = 0;
        Inv_Discount = 0;
        Inv_Tax = 0;
        Sub_Total = 0;
        Inv_Description = "";
        Inv_Opr = "";
        Inv_Odr_Code = "";
        Globals.newvehicleorderList.clear();
        Globals.vehicleorderList.clear();
        Inv_Delivery_Date = "";
    }
    public static void setEmpty1() {
        CouponTotal = 0;
        Globals.Param1 = "";
        Globals.Param2 = "";
        Globals.strContact_Name = "";
        Globals.strContact_Code = "";
        Globals.strResvContact_Code = "";
        splitPsyMd = new ArrayList<SplitPaymentList>();
        cart = new ArrayList<ShoppingCart>();
        voidcart=new ArrayList<VoidShoppingCart>();
        LoyaltyRedeem = new ArrayList<Loyalty_Redeem>();
        NetAmount = 0;
        SRNO = 1;

        InvoiceDiscount = 0;
        InvoiceTax = 0;
        TotalItemPrice = 0;
        TotalItemCost = 0;
        // TotalItem = 0;
        TotalQty = 0;
        strTable_Code = "";
        load_form_cart = "0";
        strContact_Name = "";
        NoTax="";
        Taxwith_state="";
        Globals.newvehicleorderList.clear();
        Globals.vehicleorderList.clear();
        Taxdifferent_state="";
        order_item_tax = new ArrayList<Order_Item_Tax>();
        order_tax_array = new ArrayList<OrderTaxArray>();
        strOrder_type_id = "1";
        Net_Amount = 0;
        Inv_Discount = 0;
        Inv_Tax = 0;
        Sub_Total = 0;
        Inv_Description = "";
        Inv_Opr = "";
        Inv_Odr_Code = "";
        Inv_Delivery_Date = "";
    }
    public static double TotalItemCost = 0;
    public static int TotalItem = 0;
    public static double TotalQty = 0;
    public static double ReturnTotalQty = 0;
    public static double ReturnTotalPrice = 0;
    public static double InvReturnTotalQty = 0;
    public static double InvReturnTotalPrice = 0;
    public static int TabPos = 0;
    public static double TotalItemPrice = 0;
    public static double TotalSlesPrice_CustomerDisplay=0;
    public static Lite_POS_Registration objLPR;
    public static Lite_POS_Device objLPD;

    public static String PrinterType;
    public static double CMDItemPrice = 0;
    public static String CMDItemName = "";

    /* public static String Device_Code = "35" + Build.BOARD.length() % 10
            + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10
            + Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10
            + Build.HOST.length() % 10 + Build.ID.length() % 10
            + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10
            + Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10
            + Build.TYPE.length() % 10 + Build.USER.length() % 10;*/

    public static String Device_Code = Build.SERIAL;
    public static void AppLogWrite(String text) {
        try
        {
            File logFile = new File("");
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            try {
                //BufferedWriter for performance, true to set append to file flag
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(text);
                buf.newLine();
                buf.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        catch (Exception ex)
        {

        }

    }



    public static void AppLogWrite(ArrayList<String> text) {
        try
        {
            File logFile = new File("sdcard/kitchenPrinting.file");
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            try {
                //BufferedWriter for performance, true to set append to file flag
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(text.toString());
                buf.newLine();
                buf.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        catch (Exception ex)
        {

        }

    }
    public static String myRequiredString(String strValue,int  len) {
        String strResultString ="";
        try {
                if(strValue.length() > len)
                {
                    strResultString =  strValue.substring(0,len).toString();
                }
                else
                {
                    strResultString =   strValue;
                    for(int  l=strResultString.length();l < len;l++)
                    {
                        strResultString +=" ";
                    }
                }

        } catch (Exception ex) {
            strResultString ="";
        }
        return strResultString;
    }
    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // RECREATE THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public static String myNumberFormat2Price(double abc, String decimal_check) {
        String strVal = "0";
        try {
            NumberFormat formatter = NumberFormat.getInstance(Locale.ENGLISH);
            formatter.setMaximumFractionDigits(Integer.parseInt(decimal_check));
            formatter.setMinimumFractionDigits(Integer.parseInt(decimal_check));
            formatter.setGroupingUsed(false);
            strVal = formatter.format(abc);
        } catch (Exception ex) {
        }
        return strVal;
    }

    public static String myNumberFormat2QtyDecimal(double abc, String decimal_check) {
        String strVal = "0";
        try {
            NumberFormat formatter = NumberFormat.getInstance(Locale.ENGLISH);
            formatter.setMaximumFractionDigits(Integer.parseInt(decimal_check));
            formatter.setMinimumFractionDigits(Integer.parseInt(decimal_check));
            formatter.setGroupingUsed(false);
            strVal = formatter.format(abc);
        } catch (Exception ex) {}
        return strVal;
    }

    public static String myNumberFormat2QtyDecimalstr(String decimal_check,double abc) {
        String strVal = "0";
        try {
            NumberFormat formatter = NumberFormat.getInstance(Locale.ENGLISH);
            formatter.setMaximumFractionDigits(Integer.parseInt(decimal_check));
            formatter.setMinimumFractionDigits(Integer.parseInt(decimal_check));
            formatter.setGroupingUsed(false);
            strVal = formatter.format(abc);
        } catch (Exception ex) {}
        return strVal;
    }

    public static double Denomination(Double amount, int scale, String place) {
        String amt = Globals.myNumberFormat2Price(amount, place);
        String tempResult = "";
        String part1, part2;
        if (scale == 5) {
            if (place.equals("3")) {
                String[] ab = amt.split("\\.");
                part1 = ab[0];
                part2 = ab[1];
                String a = part2.substring(0, 1);
                String b = part2.substring(1, 2);
                String c = part2.substring(2, 3);

                int c1 = Integer.parseInt(c);

                if (c1 < 3) {

                    tempResult = ab[0] + "." + a + b + "0";

                } else if (c1 < 8) {

                    tempResult = ab[0] + "." + a + b + "5";

                } else if (c1 == 8) {
                    tempResult = (amount + (0.002)) + "";
                } else if (c1 == 9) {
                    tempResult = (amount + 0.001) + "";
                }

            } else if (place.equals("2")) {
                String[] ab = amt.split("\\.");
                part1 = ab[0];
                part2 = ab[1];

                String a = part2.substring(0, 1);
                String b = part2.substring(1, 2);

                int c1 = Integer.parseInt(b);

                if (c1 < 3) {
                    tempResult = ab[0] + "." + a + "0";
                } else if (c1 < 8) {
                    tempResult = ab[0] + "." + a + "5";
                } else if (c1 == 8) {
                    tempResult = (amount + 0.02) + "";
                } else if (c1 == 9) {
                    tempResult = (amount + 0.01) + "";
                }
            }
        }


        if (scale == 1) {
            if (place.equals("3")) {

                String[] ab = amt.split("\\.");
                part1 = ab[0];
                part2 = ab[1];

                String a = part2.substring(0, 1);
                String b = part2.substring(1, 2);
//                String c = part2.substring(2, 3);

                int c1 = Integer.parseInt(part2);

                if (c1 < 499) {

                    tempResult = ab[0] + ".000";

                } else {
                    tempResult = (Integer.parseInt(part1) + 1) + ".000";

                }


            } else if (place.equals("2")) {

                String[] ab = amt.split("\\.");
                part1 = ab[0];
                part2 = ab[1];

                String a = part2.substring(0, 1);
                String b = part2.substring(1, 2);
//                String c = part2.substring(2, 3);

                int c1 = Integer.parseInt(part2);

                if (c1 < 49) {

                    tempResult = ab[0] + ".00";

                } else {
                    tempResult = (Integer.parseInt(part1) + 1) + ".00";

                }
            }


        }

        if (scale == 0) {
            if (place.equals("3")) {

                String[] ab = amt.split("\\.");
                part1 = ab[0];
                part2 = ab[1];

                String a = part2.substring(0, 1);
                String b = part2.substring(1, 2);
                String c = part2.substring(2, 3);

                int c1 = Integer.parseInt(c);

                if (c1 < 5) {

                    tempResult = ab[0] + "." + a + b + "0";

                } else if (c1 == 5) {

                    tempResult = (amount + 0.005) + "";

                } else if (c1 == 6) {

                    tempResult = (amount + 0.004) + "";

                } else if (c1 == 7) {

                    tempResult = (amount + 0.003) + "";

                } else if (c1 == 8) {

                    tempResult = (amount + 0.002) + "";

                } else if (c1 == 9) {

                    tempResult = (amount + 0.001) + "";

                }


            } else if (place.equals("2")) {

                String[] ab = amt.split("\\.");
                part1 = ab[0];
                part2 = ab[1];

                String a = part2.substring(0, 1);
                String b = part2.substring(1, 2);
                //String c = part2.substring(2, 3);

                int c1 = Integer.parseInt(b);

                if (c1 < 5) {

                    tempResult = ab[0] + "." + a + "0";

                } else if (c1 == 5) {

                    tempResult = (amount + 0.05) + "";

                } else if (c1 == 6) {

                    tempResult = (amount + 0.04) + "";

                } else if (c1 == 7) {

                    tempResult = (amount + 0.03) + "";

                } else if (c1 == 8) {

                    tempResult = (amount + 0.02) + "";

                } else if (c1 == 9) {

                    tempResult = (amount + 0.01) + "";

                }

            }
        }
        if (scale == 25) {
            // COde For Dubai Only For Two Decimal
            if (place.equals("2")) {

                String[] ab = amt.split("\\.");
                part1 = ab[0];
                part2 = ab[1];




                if (Integer.parseInt(part2) <= 12) {

                    tempResult = ab[0] + ".00";

                } else if (Integer.parseInt(part2) > 12 && Integer.parseInt(part2)<38) {

                    tempResult = ab[0] + ".25";

                } else if (Integer.parseInt(part2) > 38 && Integer.parseInt(part2)<76) {

                    tempResult = ab[0] + ".50";

                }  else if (Integer.parseInt(part2) > 75 && Integer.parseInt(part2)<99) {

                    tempResult = (Integer.parseInt(ab[0])+1)  + ".00";

                }
            }
        }


        return Double.parseDouble(tempResult);

    }


    public static NumberFormat myNumberFormat() {
        NumberFormat formatter = NumberFormat.getInstance(Locale.ENGLISH);
        formatter.setMaximumFractionDigits(0);
        formatter.setMinimumFractionDigits(0);
        formatter.setGroupingUsed(false);
        return formatter;
    }

    public static String Reportnamedate() {
        Date dt = new Date();
        SimpleDateFormat postFormater = new SimpleDateFormat("yyyyMMddHHmmss", new Locale("en"));
        return postFormater.format(dt);
    }

    public static String[] StringSplit(String original, String regex) {

        int startIndex = 0;

        @SuppressWarnings("rawtypes")
        Vector v = new Vector();

        String[] str = null;

        int index = 0;

        startIndex = original.indexOf(regex);
        //
        System.out.println("0" + startIndex);

        while (startIndex < original.length() && startIndex != -1) {
            String temp = original.substring(index, startIndex);
            System.out.println(" " + startIndex);
            // ????????
            v.addElement(temp);

            index = startIndex + regex.length();

            startIndex = original.indexOf(regex, startIndex + regex.length());
        }

        v.addElement(original.substring(index + 1 - regex.length()));

        str = new String[v.size()];
        for (int i = 0; i < v.size(); i++) {
            str[i] = (String) v.elementAt(i);
        }
        return str;
    }

    static String string;

    static String st1[] = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven",
            "Eight", "Nine",};
    static String st2[] = {"Hundred", "Thousand", "Lakh", "Crore"};
    static String st3[] = {"Ten", "Eleven", "Twelve", "Thirteen", "Fourteen",
            "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Ninteen",};
    static String st4[] = {"Twenty", "Thirty", "Fourty", "Fifty", "Sixty", "Seventy",
            "Eighty", "Ninty"};

    public static String convert(int number) {
        int n = 1;
        int word;
        string = "";
        while (number != 0) {
            switch (n) {
                case 1:
                    word = number % 100;
                    pass(word);
                    if (number > 100 && number % 100 != 0) {
                        show("and ");
                    }
                    number /= 100;
                    break;

                case 2:
                    word = number % 10;
                    if (word != 0) {
                        show(" ");
                        show(st2[0]);
                        show(" ");
                        pass(word);
                    }
                    number /= 10;
                    break;

                case 3:
                    word = number % 100;
                    if (word != 0) {
                        show(" ");
                        show(st2[1]);
                        show(" ");
                        pass(word);
                    }
                    number /= 100;
                    break;

                case 4:
                    word = number % 100;
                    if (word != 0) {
                        show(" ");
                        show(st2[2]);
                        show(" ");
                        pass(word);
                    }
                    number /= 100;
                    break;

                case 5:
                    word = number % 100;
                    if (word != 0) {
                        show(" ");
                        show(st2[3]);
                        show(" ");
                        pass(word);
                    }
                    number /= 100;
                    break;

            }
            n++;
        }
        return string;
    }

    public static void pass(int number) {
        int word, q;
        if (number < 10) {
            show(st1[number]);
        }
        if (number > 9 && number < 20) {
            show(st3[number - 10]);
        }
        if (number > 19) {
            word = number % 10;
            if (word == 0) {
                q = number / 10;
                show(st4[q - 2]);
            } else {
                q = number / 10;
                show(st1[word]);
                show(" ");
                show(st4[q - 2]);
            }
        }
    }

    public static void show(String s) {
        String st;
        st = string;
        string = s;
        string += st;
    }

    public static void ExportItem(Cursor cursor, String abc,String namestr, ArrayList<Integer> numCol, ArrayList<Integer> dateCol) {
        try {
            File exportDir = new File(Environment.getExternalStorageDirectory(), "/TriggerPOS/Exportxls");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }


            File file = new File(exportDir, abc + ".xls");
            WorkbookSettings wbSettings = new WorkbookSettings();

            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
            workbook.createSheet("Report", 0);

            WritableSheet excelSheet = workbook.getSheet(0);


            createLabel(excelSheet, cursor, abc,namestr);

            createContent(excelSheet, cursor, abc, numCol, dateCol);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            String str = e.getMessage();
            String ab = str;
        }
    }



    public static WritableCellFormat timesBoldUnderline;
    public static WritableCellFormat times;

    public static void createLabel(WritableSheet sheet, Cursor cursor, String abc,String reportname) throws WriteException {
        // Lets create a times font
        WritableFont times10pt = new WritableFont(WritableFont.TIMES, 11,
                WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE);
        // Define the cell format
        times = new WritableCellFormat(times10pt);
        // Lets automatically wrap the cells
        times.setWrap(true);

        // create create a bold font with underlines
        WritableFont times10ptBoldUnderline = new WritableFont(WritableFont.TIMES, 11, WritableFont.NO_BOLD, false,
                UnderlineStyle.NO_UNDERLINE);
        timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
        // Lets automatically wrap the cells
        timesBoldUnderline.setWrap(true);

        CellView cv = new CellView();
        cv.setFormat(times);
        cv.setFormat(timesBoldUnderline);
        cv.setAutosize(true);

        // Write a few headers

        int colCount =0;
        addCaption(sheet,0 , 0, reportname);

        int sheetcolumn=sheet.getColumns();
        sheet.mergeCells(0, 0, cursor.getColumnCount(), 0);
        while (colCount < cursor.getColumnCount()) {

            addCaption(sheet, colCount, 1, cursor.getColumnName(colCount).toString());
            colCount++;
        }


    }

    public static void addCaption(WritableSheet sheet, int column, int row, String s)
            throws RowsExceededException, WriteException {
        Label label;
        label = new Label(column, row, s, timesBoldUnderline);
        sheet.addCell(label);
    }

    public void getDatabase(){
String sqlquery= "SELECT Api_Ip from Settings";

    }



        public static int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
            int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
            return noOfColumns;
        }

    public static void createContent(WritableSheet sheet, Cursor cursor, String abc, ArrayList<Integer> numCol, ArrayList<Integer> dateCol) throws WriteException,
            RowsExceededException {

        int rowCount = 2;
int rwcount=0;
        if (cursor.moveToFirst()) {
            do {

                int colCount = 0;
                int colcnt=0;
                double count=0;
                while (colCount < cursor.getColumnCount()) {
                    if (colCount == 0) {

                        addLabel(sheet, colCount, rowCount, (rowCount) + "");
                    } else {
                        if (numCol.contains(colCount)) {
                            String decimal_check;
                            try {
                                decimal_check = Globals.objLPD.getDecimal_Place();

                            } catch (Exception ex) {
                                decimal_check = "2";
                            }
                            try {
                                //String value = Globals.myNumberFormat2Price(Double.parseDouble(cursor.getString(colCount)), decimal_check);
                                if (Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(cursor.getString(colCount)), decimal_check)) >= 0) {
                                    addNumber(sheet, colCount, rowCount, Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(cursor.getString(colCount)), decimal_check)));
//                                    addLabel(sheet, colCount, rowCount,Globals.myNumberFormat2Price(Double.parseDouble(cursor.getString(colCount)), decimal_check));

                                   // count += Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(cursor.getString(colCount)), decimal_check));
                                } else {
                                    addNumber(sheet, colCount, rowCount, Double.parseDouble("0"));
                                    //addLabel(sheet, colCount, rowCount,Globals.myNumberFormat2Price(Double.parseDouble("0"), decimal_check));
                                }
                            } catch (Exception ex) {
                                addLabel(sheet, colCount, rowCount, Globals.myNumberFormat2Price(Double.parseDouble("0"), decimal_check));
                            }
                        } else if (dateCol.contains(colCount)) {
                            addLabel(sheet, colCount, rowCount, DateUtill.PaternDate2(cursor.getString(colCount)));
                        } else {
                            addLabel(sheet, colCount, rowCount, cursor.getString(colCount));

                        }

                    }

                    // count+=Integer.parseInt(cursor.getString(colCount)); // add by jyoti
                    colCount++;
                    colcnt+=colCount;
                }
                // add by jyoti

                rowCount++;

                rwcount+=rowCount;
                int sheetRows = sheet.getRows();
                int sheetcol= sheet.getColumns();

             /*   String[][] result = new String[rowCount][];
                for (int i = 0; i < sheetRows; i++) {
                    Cell[] row = sheet.getRow(i);

                    result[i] = new String[row.length-1];
                    for (int j = 0; j < row.length; j++) {
                        result[i][j] = row[j].getContents();
                    }*/

                    StringBuffer buf = new StringBuffer();

                    buf.append(cursor.getString(cursor.getColumnIndex("Net Amount")));
                    //Formula f = new Formula(0, sheetRows+1, "Net Amount"+ buf.toString());
                     addLabel(sheet,0,sheetRows+1,"Total : "+buf.toString());
                    sheet.mergeCells(0, sheetRows+1, sheetcol+1, sheetRows+1);
                //sheet.mergeCells(sheetRows+1,sheetRows+1,0,colCount+1);
                    //sheet.addCell(f);




            } while (cursor.moveToNext());

        }
    }

    public static void addLabel(WritableSheet sheet, int column, int row, String s)
            throws WriteException, RowsExceededException {
        Label label;
        label = new Label(column, row, s, times);
        sheet.addCell(label);
    }

    public static void addNumber(WritableSheet sheet, int column, int row, Double aDouble) throws WriteException, RowsExceededException {
        Number number;
        number = new Number(column, row, aDouble, times);
        sheet.addCell(number);
    }


    public static void SetOrderDiscount(Context context, String strDisValue, String strType, Database db, SQLiteDatabase database) {
        Globals.order_tax_array.clear();
        Globals.order_item_tax.clear();
        Double total = 0d;
        Double final_netamount = 0d;
        Double totalDisnt = 0d;
        Double showDiscount = 0d;
        Double priceAfDis = 0d;
        ArrayList<ShoppingCart> myCart = Globals.cart;
        ArrayList<Order_Type_Tax> order_type_taxArrayList;
        for (int count = 0; count < myCart.size(); count++) {
            myCart.get(count);
            Double lineTotal = Double.parseDouble(myCart.get(count).get_Sales_Price());
            showDiscount = ((lineTotal) * Double.parseDouble(strDisValue) / 100);
            priceAfDis = Double.parseDouble(myCart.get(count).get_Sales_Price()) - showDiscount;
            myCart.get(count).set_Discount(Double.parseDouble(strDisValue) + "");
            totalDisnt = totalDisnt + ((Double.parseDouble(myCart.get(count).get_Sales_Price()) - priceAfDis) * Double.parseDouble(myCart.get(count).get_Quantity()));
            ArrayList<Item_Group_Tax> item_group_taxArrayList = Item_Group_Tax.getAllItem_Group_Tax(context, "Where item_group_code = '" + myCart.get(count).get_Item_Code() + "'", database, db);
            Double iTax = 0d;
            Double iTaxTotal = 0d;
            for (int i = 0; i < item_group_taxArrayList.size(); i++) {
                iTax = 0d;
                Item_Group_Tax item_group_tax = item_group_taxArrayList.get(i);
                String tax_id = item_group_tax.get_tax_id();
                Tax_Master tax_master = Tax_Master.getTax_Master(context, "Where tax_id = '" + tax_id + "'", database, db);
                if (tax_master.get_tax_type().equals("P")) {
                    iTax = iTax + (priceAfDis * Double.parseDouble(tax_master.get_rate()) / 100);
                } else {
                    //Double.parseDouble(Globals.myNumberFormat2Price(iTax,decimal_check));
                    iTax = iTax + Double.parseDouble(tax_master.get_rate());
                }


                iTaxTotal = iTaxTotal + Double.parseDouble(Globals.myNumberFormat2Price(iTax, Globals.objLPD.getDecimal_Place()));

                Order_Item_Tax order_item_tax = new Order_Item_Tax(context, "", "", Globals.SRNO + "", myCart.get(count).get_Item_Code(), tax_id, tax_master.get_tax_type(), tax_master.get_rate(), Globals.myNumberFormat2Price(iTax, Globals.objLPD.getDecimal_Place()) + "");
                Globals.order_item_tax.add(order_item_tax);
            }
            myCart.get(count).set_Tax_Price(iTaxTotal + "");
            myCart.get(count).set_Line_Total((Double.parseDouble(myCart.get(count).get_Quantity()) * (priceAfDis + Double.parseDouble(myCart.get(count).get_Tax_Price()))) + "");
            final_netamount = final_netamount + Double.parseDouble(myCart.get(count).get_Line_Total());
        }


        Globals.cart = myCart;
        Double iPrice = 0d;
        order_type_taxArrayList = Order_Type_Tax.getAllOrder_Type_Tax(context, "WHERE order_type_id = '" + Globals.strOrder_type_id + "' ", database);
        Double iTax = 0.0;
        if (order_type_taxArrayList.size() > 0) {
            for (int i = 0; i < order_type_taxArrayList.size(); i++) {

                Order_Type_Tax order_type_tax = order_type_taxArrayList.get(i);
                String tax_id = order_type_tax.get_tax_id();

                Tax_Master tax_master = Tax_Master.getTax_Master(context, "Where tax_id = '" + tax_id + "'", database, db);

                iPrice = final_netamount;

                Double itempTax = 0d;
                if (tax_master.get_tax_type().equals("P")) {
                    iTax = iTax + (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);
                    itempTax = (iPrice * Double.parseDouble(tax_master.get_rate()) / 100);

                } else {
                    iTax = iTax + Double.parseDouble(Globals.myNumberFormat2Price(Double.parseDouble(tax_master.get_rate()), Globals.objLPD.getDecimal_Place()));
                    itempTax = Double.parseDouble(tax_master.get_rate());
                }

                OrderTaxArray orderTaxArray = new OrderTaxArray(context, "", "", tax_id, tax_master.get_tax_type(), tax_master.get_rate(), itempTax + "");
                Globals.order_tax_array.add(orderTaxArray);
            }

            total = iPrice + iTax;


        } else {

            iPrice = Double.parseDouble(Globals.TotalItemPrice + "");
            iPrice = Double.parseDouble(final_netamount + "");
            total = iPrice;


        }


    }
    public static String export_sample() {

        String strResult = "";


        //  item_code,item_group_code,manufacture_code,item_name,description,sku,barcode,unit_id,hsn_sac_code,is_inclusive_tax ";


        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File file = new File(exportDir, "" + "table_export_sample" + ".csv");
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            //SQLiteDatabase sqlite = db.getReadableDatabase();

            List<ListSampleTable> list_sampleitem = new ArrayList<>();
            ListSampleTable listSampleItem = new ListSampleTable("table_code", "table_name");
            list_sampleitem.add((listSampleItem));

            listSampleItem = new ListSampleTable("T1", "Table1");
            list_sampleitem.add((listSampleItem));
            listSampleItem = new ListSampleTable("T2", "Table2");
            list_sampleitem.add((listSampleItem));


            int RowCount = 0;


            //  csvWrite.writeNext(curCSV.getColumnNames());
            while (RowCount < list_sampleitem.size()) {

                ListSampleTable listSampleItem1 = list_sampleitem.get(RowCount);

                ArrayList<String> stringArrayList = new ArrayList<String>();
                int columncount = 13;

                for (int i = 0; i < columncount; i++) {

                    switch (i) {
                        case 0:
                            stringArrayList.add(listSampleItem1.table_code);
                            break;
                        case 1:
                            stringArrayList.add(listSampleItem1.table_name);
                            break;

                    }


                }
                //Which column you want to exprort
                String[] stringArray = stringArrayList.toArray(new String[stringArrayList.size()]);

                csvWrite.writeNext(stringArray);
                RowCount++;
            }
            csvWrite.close();

            //csvWrite.close();

            strResult = "success";

            //Toast.makeText(getApplicationContext(), getString(R.string.exportedcsv), Toast.LENGTH_SHORT).show();
        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);

        }

        return strResult;

    }

}

class ListSampleTable{
    public String table_code;
    public String table_name;
    // public String manufacture_code;

//    public String tax3;

    public ListSampleTable(String t1, String t2) {

        this.table_code = t1;
        this.table_name = t2;

    }
}
