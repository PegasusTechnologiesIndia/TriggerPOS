package org.phomellolitepos.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.widget.Toast;

import org.phomellolitepos.R;
import org.phomellolitepos.Util.DateUtill;

import javax.mail.Folder;


public class Database extends SQLiteOpenHelper {
    private static final int version = 76;

    public static final String DATABASE_FILE_PATH = Environment.getExternalStorageDirectory() +
            File.separator + "Phomello-LitePOS";

    Cursor cursor;
    public final static String DBNAME = "LitePOS.db";
    Context context;

    SQLiteDatabase database;
    Database db;

    public final int conflictAlgo_Roll = SQLiteDatabase.CONFLICT_ROLLBACK;
    public final int conflictAlgo_Fail = SQLiteDatabase.CONFLICT_FAIL;
    public final int conflictAlgo_IGNORE = SQLiteDatabase.CONFLICT_IGNORE;
    public final int conflictAlgo_ABORT = SQLiteDatabase.CONFLICT_ABORT;

    public Database(Context context, String name, int version) {
        super(context, name, null, version);
    }

    public Database(Context context) {
        super(context, DATABASE_FILE_PATH + File.separator + DBNAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            DatabaseBackUp();
            switch (oldVersion) {
//            switch (newVersion) {

                case 24:     // for database version = 1 create when install
                    try {

                        arg0.execSQL("ALTER TABLE orders ADD COLUMN delivery_date DATETIME");
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Is_Denomination BOOLEAN");
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Is_BarcodePrint BOOLEAN");
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Is_Discount BOOLEAN");
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Gst_No NVARCHAR(20)");
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Print_Order NVARCHAR(20)");
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Print_Cashier NVARCHAR(20)");
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Print_InvNo NVARCHAR(20)");
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Print_InvDate NVARCHAR(20)");
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Print_DeviceID NVARCHAR(20)");

                        arg0.execSQL("UPDATE Settings SET Is_Denomination ='false',Is_BarcodePrint='false',Is_Discount='false',Gst_No='TRN',Print_Order='TAX INVOICE',Print_Cashier='Salesperson',Print_InvNo='Invoice Number',Print_InvDate='Invoice Date',Print_DeviceID='Device ID'");
                        arg0.execSQL("CREATE TABLE IF NOT EXISTS Sys_Support (id INTEGER PRIMARY KEY AUTOINCREMENT, name NVARCHAR(50), vedio_url NVARCHAR(150))");

                        arg0.execSQL("CREATE TABLE IF NOT EXISTS Sys_Sycntime ( id INTEGER PRIMARY KEY AUTOINCREMENT,  table_name NVARCHAR(50), datetime DATETIME)");
                        arg0.execSQL("CREATE TABLE IF NOT EXISTS Sys_Tax_Group ( id INTEGER PRIMARY KEY AUTOINCREMENT, tax_master_id INTEGER(11), tax_id INTEGER(11))");
                        arg0.execSQL("CREATE TABLE IF NOT EXISTS Bank( bank_id INTEGER PRIMARY KEY AUTOINCREMENT, device_code NVARCHAR(50), bank_code NVARCHAR(50), bank_name NVARCHAR(50),email NVARCHAR(50), mobile NVARCHAR(50), address NVARCHAR(50),bank_ref_code NVARCHAR(50), is_active  BOOLEAN,modified_by NVARCHAR(50), modified_date DATETIME, is_push BOOLEAN)");

                        arg0.execSQL("INSERT into Sys_Sycntime (table_name,datetime) values ('item','1990-01-01')");
                        arg0.execSQL("INSERT into Sys_Sycntime (table_name,datetime) values ('item_group','1990-01-01')");
                        arg0.execSQL("INSERT into Sys_Sycntime (table_name,datetime) values ('contact','1990-01-01')");
                        arg0.execSQL("INSERT into Sys_Sycntime (table_name,datetime) values ('business_group','1990-01-01')");
                        arg0.execSQL("INSERT into Sys_Sycntime (table_name,datetime) values ('tax','1990-01-01')");
                        arg0.execSQL("INSERT into Sys_Sycntime (table_name,datetime) values ('manufacture','1990-01-01')");
                        arg0.execSQL("INSERT into Sys_Support (name,vedio_url) values ('Phomello LitePOS Manager','https://www.youtube.com/watch?v=GlAFWX3VSzQ')");
                        arg0.execSQL("INSERT into Sys_Support (name,vedio_url) values ('Business Group Creation and Modification','https://www.youtube.com/watch?v=StBQ2n3_Lyo')");
                        arg0.execSQL("INSERT into Sys_Support (name,vedio_url) values ('Contact creation and Modification','https://www.youtube.com/watch?v=wTSi5-lG7Og')");
                        arg0.execSQL("INSERT into Sys_Support (name,vedio_url) values ('Tax Creation and Modification','https://www.youtube.com/watch?v=CWziXsSHPPI')");
                        arg0.execSQL("INSERT into Sys_Support (name,vedio_url) values ('Creating an order','https://www.youtube.com/watch?v=dPkSh8lVLIQ')");
                        arg0.execSQL("INSERT into Sys_Support (name,vedio_url) values ('Item Category Creation and Modication','https://www.youtube.com/watch?v=qlDRadfOBPQ')");
                        arg0.execSQL("INSERT into Sys_Support (name,vedio_url) values ('Profile and License Update','https://www.youtube.com/watch?v=T7OV10m8SZI')");
                        arg0.execSQL("INSERT into Sys_Support (name,vedio_url) values ('Receipt reprint cancel and save','https://www.youtube.com/watch?v=hGoYroSyoxc')");
                        arg0.execSQL("INSERT into Sys_Support (name,vedio_url) values ('Reports Details','https://www.youtube.com/watch?v=LtoXWqUsVws')");
                        arg0.execSQL("INSERT into Sys_Support (name,vedio_url) values ('Phomello LitePOS Settings','https://www.youtube.com/watch?v=i3wAQ-K7vTA')");
                        arg0.execSQL("ALTER TABLE item ADD COLUMN item_image NVARCHAR(50)");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                case 50:

                    try {
                        arg0.execSQL("ALTER TABLE User ADD COLUMN app_user_permission NVARCHAR(150)");
                        arg0.execSQL("UPDATE User SET app_user_permission=''");

                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Is_KOT_Print BOOLEAN");
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Is_Print_Invoice BOOLEAN");
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Is_File_Share BOOLEAN");

                        arg0.execSQL("UPDATE Settings SET Is_KOT_Print='false'");
                        arg0.execSQL("UPDATE Settings SET Is_Print_Invoice='false'");
                        arg0.execSQL("UPDATE Settings SET Is_File_Share='false'");
                        //arg0.execSQL("CREATE TABLE IF NOT EXISTS Acc_Customer_Credit( id INTEGER PRIMARY KEY AUTOINCREMENT, trans_date DATETIME, contact_code NVARCHAR(50), cr_amount DECIMAL(18, 6),paid_amount DECIMAL(18, 6), balance_amount DECIMAL(18, 6), z_no NVARCHAR(50), is_active  BOOLEAN,modified_by NVARCHAR(50), modified_date DATETIME)");
                    } catch (Exception ex) {
                    }
                case 51:
                    //13-03-2018
                    try {

                        arg0.execSQL("ALTER TABLE unit ADD COLUMN is_push BOOLEAN");
                        arg0.execSQL("UPDATE unit SET is_push='N'");
                        arg0.execSQL("CREATE TABLE IF NOT EXISTS Acc_Customer_Credit( id INTEGER PRIMARY KEY AUTOINCREMENT, trans_date DATETIME, contact_code NVARCHAR(50), cr_amount DECIMAL(18, 6),paid_amount DECIMAL(18, 6), balance_amount DECIMAL(18, 6), z_no NVARCHAR(50), is_active  BOOLEAN,modified_by NVARCHAR(50), modified_date DATETIME)");
                    } catch (Exception ex) {
                    }
                case 52:
                    //15-03-2018
                    try {
                        DatabaseBackUp();
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Host NVARCHAR(50)");
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Port NVARCHAR(50)");
                        arg0.execSQL("UPDATE Settings SET Host=''");
                        arg0.execSQL("UPDATE Settings SET Port=''");
                    } catch (Exception ex) {
                    }
                case 53:
                    //22-03-2018

                    try {
                        arg0.execSQL("CREATE TABLE IF NOT EXISTS acc_customer_dedit( id INTEGER PRIMARY KEY AUTOINCREMENT, order_code NVARCHAR(50), amount DECIMAL(18, 6),z_no NVARCHAR(50), ref_type NVARCHAR(2))");

                    } catch (Exception ex) {
                    }

                case 54:
                    //22-03-2018

                    try {
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Change_Parameter NVARCHAR(50)");
                        arg0.execSQL("UPDATE Settings SET Change_Parameter='AC'");
                    } catch (Exception ex) {
                    }

                case 55:
                    //03-04-2018

                    try {
                        arg0.execSQL("CREATE TABLE IF NOT EXISTS customer_price_book( id INTEGER PRIMARY KEY AUTOINCREMENT, contact_code NVARCHAR(50), item_code NVARCHAR(50), sale_price DECIMAL(18, 6),modified_date DATETIME)");
                    } catch (Exception ex) {
                    }

                case 56:
                    //05-04-2018

                    try {
                        arg0.execSQL("CREATE TABLE IF NOT EXISTS stock_adjustment_header(id INTEGER PRIMARY KEY AUTOINCREMENT, voucher_no NVARCHAR(50),date DATETIME,remarks NVARCHAR(50),is_post BOOLEAN,is_cancel BOOLEAN,is_active BOOLEAN,modified_by NVARCHAR(50),modified_date DATETIME)");
                        arg0.execSQL("CREATE TABLE IF NOT EXISTS stock_adjustment_detail( id INTEGER PRIMARY KEY AUTOINCREMENT, ref_voucher_no NVARCHAR(50), s_no INTEGER(11), item_code Varchar(50),qty Decimal(18, 6),in_out_flag Varchar(1))");
                    } catch (Exception ex) {
                    }

                case 57:
                    //06-04-2018

                    try {
                        arg0.execSQL("CREATE TABLE IF NOT EXISTS returns(id INTEGER PRIMARY KEY AUTOINCREMENT, contact_code NVARCHAR(50), voucher_no NVARCHAR(50),date DATETIME,remarks NVARCHAR(50),total DECIMAL(18, 6),z_code NVARCHAR(50),is_post BOOLEAN,is_cancel BOOLEAN,is_active BOOLEAN,is_push BOOLEAN,modified_by NVARCHAR(50),modified_date DATETIME)");
                        arg0.execSQL("CREATE TABLE IF NOT EXISTS return_detail(id INTEGER PRIMARY KEY AUTOINCREMENT, ref_voucher_no NVARCHAR(50), s_no INTEGER(11), item_code Varchar(50),qty Decimal(18, 6),price Decimal(18, 6),line_total Decimal(18, 6))");
                    } catch (Exception ex) {
                    }

                case 58:
                    //06-04-2018
                    try {
                        arg0.execSQL("CREATE TABLE IF NOT EXISTS purchase(id INTEGER PRIMARY KEY AUTOINCREMENT, contact_code NVARCHAR(50), voucher_no NVARCHAR(50), ref_voucher_code NVARCHAR(50),date DATETIME,remarks NVARCHAR(50),total DECIMAL(18, 6),is_post BOOLEAN,is_cancel BOOLEAN,is_active BOOLEAN,is_push BOOLEAN,modified_by NVARCHAR(50),modified_date DATETIME)");
                        arg0.execSQL("CREATE TABLE IF NOT EXISTS purchase_detail(id INTEGER PRIMARY KEY AUTOINCREMENT, ref_voucher_no NVARCHAR(50), s_no INTEGER(11), item_code Varchar(50),qty Decimal(18, 6),price Decimal(18, 6),line_total Decimal(18, 6))");
                        arg0.execSQL("CREATE TABLE IF NOT EXISTS purchase_payment([id] INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                                "    [device_code] NVARCHAR(50),\n" +
                                "    [ref_voucher_no] NVARCHAR(50),\n" +
                                "    [sr_no] INTEGER(11),\n" +
                                "    [pay_amount] [DECIMAL(15,3)],\n" +
                                "    [payment_id] INTEGER(11),\n" +
                                "    [currency_id] INTEGER(11),\n" +
                                "    [currency_value] [DECIMAL(15,6)],\n" +
                                "    [card_number] NVARCHAR(50),\n" +
                                "    [card_name] NVARCHAR(50),\n" +
                                "    [field1] TEXT,\n" +
                                "    [field2] TEXT)");
                    } catch (Exception ex) {
                    }

                case 59:
                    //22-03-2018

                    try {
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Is_Accounts BOOLEAN");
                        arg0.execSQL("UPDATE Settings SET Is_Accounts='false'");
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Is_Stock_Manager BOOLEAN");
                        arg0.execSQL("UPDATE Settings SET Is_Stock_Manager='false'");
                    } catch (Exception ex) {
                    }

                    try {
                        arg0.execSQL("CREATE TABLE IF NOT EXISTS acc_customer(id INTEGER PRIMARY KEY AUTOINCREMENT,contact_code NVARCHAR(50), amount DECIMAL(18,6))");
                    } catch (Exception ex) {
                    }

                case 60:
                    //19-04-2018

                    try {
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Home_Layout NVARCHAR(50)");
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Is_Cash_Drawer BOOLEAN");
                        arg0.execSQL("UPDATE Settings SET Home_Layout='0'");
                        arg0.execSQL("UPDATE Settings SET Is_Cash_Drawer='false'");

                    } catch (Exception ex) {
                    }

                case 61:
                    //19-04-2018

                    try {

                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Is_Zero_Stock BOOLEAN");
                        arg0.execSQL("UPDATE Settings SET Is_Zero_Stock = 'true'");

                    } catch (Exception ex) {
                    }

                case 62:
                    //19-04-2018

                    try {

                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Is_Change_Price BOOLEAN");
                        arg0.execSQL("UPDATE Settings SET Is_Change_Price = 'false'");

                    } catch (Exception ex) {
                    }

                case 63:
                    //19-04-2018

                    try {

//                        arg0.execSQL("ALTER TABLE [z_detail] ADD CONSTRAINT fk_z_close\n" +
//                                "                        FOREIGN KEY (z_code)\n" +
//                                "                                REFERENCES z_close(z_code)");

                        arg0.execSQL("DROP INDEX IF EXISTS [IX_ACC_CUSTOMER_CREDIT]");
                        arg0.execSQL("CREATE INDEX `IX_ACC_CUSTOMER_CREDIT` ON `Acc_Customer_Credit` (`contact_code` ASC, `z_no` ASC )");

                        arg0.execSQL("DROP INDEX IF EXISTS [ix_acc_customer_debit]");
                        arg0.execSQL("CREATE INDEX [ix_acc_customer_debit]\n" +
                                "ON [acc_customer_dedit](\n" +
                                "    [z_no] ASC, \n" +
                                "    [order_code] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [ix_z_detail]");
                        arg0.execSQL("CREATE INDEX [ix_z_detail]\n" +
                                "ON [z_detail](\n" +
                                "    [z_code] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [ix_zone]");
                        arg0.execSQL("CREATE INDEX [ix_zone]\n" +
                                "ON [zone](\n" +
                                "    [country_id] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [ix_tax_detail]");
                        arg0.execSQL("CREATE INDEX [ix_tax_detail]\n" +
                                "ON [Tax_Detail](\n" +
                                "    [tax_type_id] ASC, \n" +
                                "    [tax_id] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [ix_sys_support]");
                        arg0.execSQL("CREATE INDEX [ix_sys_support]\n" +
                                "ON [Sys_Support](\n" +
                                "    [id] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [ix_sys_synchtime]");
                        arg0.execSQL("CREATE INDEX [ix_sys_synchtime]\n" +
                                "ON [Sys_Sycntime](\n" +
                                "    [id] ASC, \n" +
                                "    [table_name] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [ux_stock_adjustmenr_header]");
                        arg0.execSQL("CREATE UNIQUE INDEX [ux_stock_adjustmenr_header]\n" +
                                "ON [stock_adjustment_header](\n" +
                                "    [voucher_no] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [ix_stock_adjustment_header]");
                        arg0.execSQL("CREATE INDEX [ix_stock_adjustment_header]\n" +
                                "ON [stock_adjustment_header](\n" +
                                "    [is_post] ASC, \n" +
                                "    [is_active] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [uk_stock_adjustment_detail]");
                        arg0.execSQL("CREATE UNIQUE INDEX [uk_stock_adjustment_detail]\n" +
                                "ON [stock_adjustment_detail](\n" +
                                "    [ref_voucher_no] ASC, \n" +
                                "    [s_no] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [ix_returns]");
                        arg0.execSQL("CREATE INDEX [ix_returns]\n" +
                                "ON [returns](\n" +
                                "    [contact_code] ASC, \n" +
                                "    [date] ASC, \n" +
                                "    [z_code] ASC, \n" +
                                "    [is_post] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [uk_returns]");
                        arg0.execSQL("CREATE UNIQUE INDEX [uk_returns]\n" +
                                "ON [returns](\n" +
                                "    [voucher_no] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [ix_stock_adjustment_detail]");
                        arg0.execSQL("CREATE INDEX [ix_stock_adjustment_detail]\n" +
                                "ON [stock_adjustment_detail](\n" +
                                "    [item_code] ASC, \n" +
                                "    [in_out_flag] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [ix_return_detail]");
                        arg0.execSQL("CREATE INDEX [ix_return_detail]\n" +
                                "ON [return_detail](\n" +
                                "    [item_code] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [uk_return_detail]");
                        arg0.execSQL("CREATE UNIQUE INDEX [uk_return_detail]\n" +
                                "ON [return_detail](\n" +
                                "    [ref_voucher_no] ASC, \n" +
                                "    [s_no] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [ix_reservation]");
                        arg0.execSQL("CREATE INDEX [ix_reservation]\n" +
                                "ON [Reservation](\n" +
                                "    [date_time] ASC, \n" +
                                "    [customer_code] ASC, \n" +
                                "    [user_code] ASC, \n" +
                                "    [table_code] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [uk_purchase]");
                        arg0.execSQL("CREATE UNIQUE INDEX [uk_purchase]\n" +
                                "ON [purchase](\n" +
                                "    [voucher_no] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [ix_purchase]");
                        arg0.execSQL("CREATE INDEX [ix_purchase]\n" +
                                "ON [purchase](\n" +
                                "    [contact_code] ASC, \n" +
                                "    [date] ASC, \n" +
                                "    [is_post] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [uk_purcahse_detail]");
                        arg0.execSQL("CREATE UNIQUE INDEX [uk_purcahse_detail]\n" +
                                "ON [purchase_detail](\n" +
                                "    [ref_voucher_no] ASC, \n" +
                                "    [s_no] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [uk_purchase_payment]");
                        arg0.execSQL("CREATE UNIQUE INDEX [uk_purchase_payment]\n" +
                                "ON [purchase_payment](\n" +
                                "    [ref_voucher_no] ASC, \n" +
                                "    [sr_no] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [uk_pos_balance]");
                        arg0.execSQL("CREATE UNIQUE INDEX [uk_pos_balance]\n" +
                                "ON [pos_balance](\n" +
                                "    [pos_balance_code] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [ix_order_type_tax]");
                        arg0.execSQL("CREATE INDEX [ix_order_type_tax]\n" +
                                "ON [order_type_tax](\n" +
                                "    [location_id] ASC, \n" +
                                "    [order_type_id] ASC, \n" +
                                "    [tax_id] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [ux_order_payment]");
                        arg0.execSQL("CREATE INDEX [ux_order_payment]\n" +
                                "ON [order_payment](\n" +
                                "    [order_code] ASC, \n" +
                                "    [sr_no] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [uk_customer_price_book]");
                        arg0.execSQL("CREATE UNIQUE INDEX [uk_customer_price_book]\n" +
                                "ON [customer_price_book](\n" +
                                "    [contact_code] ASC, \n" +
                                "    [item_code] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [IX_z_close]");
                        arg0.execSQL("CREATE INDEX [IX_z_close]\n" +
                                "ON [z_close](\n" +
                                "    [z_no], \n" +
                                "    [is_active], \n" +
                                "    [is_push])");

                        arg0.execSQL("DROP INDEX IF EXISTS [ix_user_id]");
                        arg0.execSQL("CREATE INDEX [ix_user_id]\n" +
                                "ON [user](\n" +
                                "    [user_code] ASC, \n" +
                                "    [name] ASC, \n" +
                                "    [password], \n" +
                                "    [is_active], \n" +
                                "    [is_push])");

                        arg0.execSQL("DROP INDEX IF EXISTS [ix_unit]");
                        arg0.execSQL("CREATE INDEX [ix_unit]\n" +
                                "ON [unit](\n" +
                                "    [unit_id] ASC, \n" +
                                "    [code] ASC, \n" +
                                "    [is_active] ASC, \n" +
                                "    [is_push] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [ix_tax]");
                        arg0.execSQL("CREATE INDEX [ix_tax]\n" +
                                "ON [tax](\n" +
                                "    [location_id] ASC, \n" +
                                "    [tax_id] ASC, \n" +
                                "    [is_active], \n" +
                                "    [is_push])");

                        arg0.execSQL("DROP INDEX IF EXISTS [ix_order_detail_tax]");
                        arg0.execSQL("CREATE INDEX [ix_order_detail_tax]\n" +
                                "ON [order_detail_tax](\n" +
                                "    [order_code] ASC, \n" +
                                "    [item_code] ASC, \n" +
                                "    [tax_id] ASC)");

                        arg0.execSQL("DROP INDEX IF EXISTS [ix_item_location]");
                        arg0.execSQL("CREATE UNIQUE INDEX [ix_item_location]\n" +
                                "ON [item_location](\n" +
                                "    [item_code], \n" +
                                "    [location_id])");


                    } catch (Exception ex) {
                        String ab = ex.getMessage();
                        ab = ab;

                    }

                case 64:
                    //19-04-2018

                    try {
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN No_Of_Print NVARCHAR(50)");
                        arg0.execSQL("UPDATE Settings SET No_Of_Print = '1'");
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN GST_Label NVARCHAR(50)");
                        arg0.execSQL("UPDATE Settings SET GST_Label = 'GST'");
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Is_ZDetail_InPrint BOOLEAN");
                        arg0.execSQL("UPDATE Settings SET Is_ZDetail_InPrint = 'false'");
                    } catch (Exception ex) {
                    }


                case 65:
                    //2018-06-06

                    try {
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Is_Device_Customer_Show BOOLEAN");
                        arg0.execSQL("UPDATE Settings SET Is_Device_Customer_Show = 'false'");
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Is_Print_Dialog_Show BOOLEAN");
                        arg0.execSQL("UPDATE Settings SET Is_Print_Dialog_Show = 'true'");

                    } catch (Exception ex) {
                    }

                case 66:
                    //2018-06-06

                    try {
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Is_BR_Scanner_Show BOOLEAN");
                        arg0.execSQL("UPDATE Settings SET Is_BR_Scanner_Show = 'false'");

                    } catch (Exception ex) {
                    }

                case 67:
                    //2018-06-06

                    try {
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Default_Ordertype NVARCHAR(2)");
                        arg0.execSQL("UPDATE Settings SET Default_Ordertype = '1'");

                    } catch (Exception ex) {
                    }

                case 68:
                    //2018-06-06

                    try {
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Print_Memo NVARCHAR(2)");
                        arg0.execSQL("UPDATE Settings SET Print_Memo = '0'");

                    } catch (Exception ex) {
                    }

                case 69:

                    try {
                        arg0.execSQL("CREATE TABLE IF NOT EXISTS ticket_setup([id] INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                                "    [menufacture_id] NVARCHAR(50),\n" +
                                "    [tck_from] NVARCHAR(150),\n" +
                                "    [tck_to] NVARCHAR(150),\n" +
                                "    [price] DECIMAL(18, 6),\n" +
                                "    [departure] DATETIME,\n" +
                                "    [arrival] DATETIME,\n" +
                                "    [is_inclusive_tax] BOOLEAN,\n" +
                                "    [new_price] DECIMAL(18, 6),\n" +
                                "    [bus_number] NVARCHAR(150),\n" +
                                "    [is_active] BOOLEAN)");


                        arg0.execSQL("CREATE TABLE IF NOT EXISTS ticket_setup_category([id] INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                                "        [ref_id] NVARCHAR(50),\n" +
                                "        [category_id] NVARCHAR(50))");

                        arg0.execSQL("CREATE TABLE IF NOT EXISTS ticket_setup_days([id] INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                                "        [ref_id] NVARCHAR(50),\n" +
                                "        [days] NVARCHAR(50))");

                        arg0.execSQL("CREATE TABLE IF NOT EXISTS ticket_setup_tax([id] INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                                "         [ref_id] NVARCHAR(50),\n" +
                                "         [tax_id] NVARCHAR(50))");


                    } catch (Exception ex) {
                    }


                case 70:
                    try {
                        arg0.execSQL("CREATE TABLE [pro_loyalty_setup](\n" +
                                "    [id] INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                                "    [min_purchase_value] DECIMAL(18, 6),\n" +
                                "    [base_value] DECIMAL(18, 6),\n" +
                                "    [earn_point] INTEGER(11),\n" +
                                "    [earn_value] DECIMAL(18, 6),\n" +
                                "    [mis_redeem_value] DECIMAL(18, 6),\n" +
                                "    [loyalty_type] NVARCHAR(150),\n" +
                                "    [name] NVARCHAR(150),\n" +
                                "    [valid_from] DATETIME,\n" +
                                "    [valid_to] DATETIME)");

                        arg0.execSQL("CREATE TABLE [order_loyalty_earn](\n" +
                                "                  [id] INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                                "                  [order_code] NVARCHAR(50),\n" +
                                "                  [customer_code] NVARCHAR(50),\n" +
                                "                  [earn_point] INTEGER(11),\n" +
                                "                  [earn_value] DECIMAL(18, 6),\n" +
                                "                  [redeem_point] DECIMAL(18, 6))");

                    } catch (Exception ex) {

                    }

                case 71:
                    try {

                    } catch (Exception ex) {
                    }

                case 72:
                    try {
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN Is_Cost_Show BOOLEAN");
                        arg0.execSQL("UPDATE Settings SET Is_Cost_Show = 'false'");
                    } catch (Exception ex) {}

                case 73:
                    try {
                        arg0.execSQL("ALTER TABLE returns ADD COLUMN order_code NVARCHAR(50)");
                        arg0.execSQL("UPDATE returns SET order_code = ''");
                        arg0.execSQL("ALTER TABLE returns ADD COLUMN return_type NVARCHAR(2)");
                        arg0.execSQL("UPDATE returns SET return_type = ''");
                    } catch (Exception ex) {}

                case 74:
                    try {
                        arg0.execSQL("ALTER TABLE Settings ADD COLUMN QR_Type NVARCHAR(2)");
                        arg0.execSQL("UPDATE Settings SET QR_Type = '0'");
                    } catch (Exception ex) {}

                case 75:
                    try {
                        arg0.execSQL("ALTER TABLE returns ADD COLUMN payment_id NVARCHAR(2)");
                        arg0.execSQL("UPDATE returns SET payment_id = ''");
                    } catch (Exception ex) {}

                case 76:
                    try {
                        arg0.execSQL("ALTER TABLE Lite_POS_Device ADD COLUMN lic_customer_license_id TEXT");
                        arg0.execSQL("UPDATE Lite_POS_Device SET lic_customer_license_id = ''");
                        arg0.execSQL("ALTER TABLE Lite_POS_Device ADD COLUMN lic_code TEXT");
                        arg0.execSQL("UPDATE Lite_POS_Device SET lic_code = ''");
                        arg0.execSQL("ALTER TABLE Lite_POS_Device ADD COLUMN license_key TEXT");
                        arg0.execSQL("UPDATE Lite_POS_Device SET license_key = ''");
                        arg0.execSQL("ALTER TABLE Lite_POS_Device ADD COLUMN license_type TEXT");
                        arg0.execSQL("UPDATE Lite_POS_Device SET license_type = ''");
                        arg0.execSQL("ALTER TABLE Lite_POS_Device ADD COLUMN Status TEXT");
                        arg0.execSQL("UPDATE Lite_POS_Device SET Status = ''");
                        arg0.execSQL("ALTER TABLE last_code ADD COLUMN last_order_return_code NVARCHAR(50)");
                        arg0.execSQL("UPDATE last_code SET last_order_return_code = ''");
                    } catch (Exception ex) {}
            }
        }
    }

    private String DatabaseBackUp() {
        File backupDB = null;
        String backupDBPath = null;
        try {
            File sd = new File(Environment.getExternalStorageDirectory(), "LitePOS/Backup");

            if (!sd.exists()) {
                sd.mkdirs();
            }
            FileChannel source = null;
            FileChannel destination = null;
            String currentDBPath = Database.DATABASE_FILE_PATH + File.separator + Database.DBNAME;
            backupDBPath = "LitePOS" + DateUtill.currentDatebackup() + ".db";
            File currentDB = new File(currentDBPath);
            backupDB = new File(sd, backupDBPath);
            try {
                source = new FileInputStream(currentDB).getChannel();
                destination = new FileOutputStream(backupDB).getChannel();
                destination.transferFrom(source, 0, source.size());
                source.close();
                destination.close();

            } catch (IOException e) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        return backupDBPath;
    }

    public Cursor getCursor(String sql) {
        SQLiteDatabase database = this.getReadableDatabase();
        return database.rawQuery(sql, null);
    }


    public static CharSequence getCountryName(String _id, Context context2) {
        String name = "";
        Database db = new Database(context2);
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cur = database.rawQuery(
                "SELECT CountryName From Sys_CountryMaster where  Sys_CountryMaster.CountryID="
                        + _id, null);
        if (cur.moveToFirst()) {
            name = cur.getString(0);
        }
        cur.close();
        return name;
    }

    public static void DropTable(Context context2, String TableName) {
        Database db = new Database(context2);
        SQLiteDatabase sdb = db.getReadableDatabase();
        sdb.execSQL("DELETE TABLE IF EXISTS " + TableName);
        //sdb.close();
    }

    public static void CreateTable(Context context, String Query) {

        Database db = new Database(context);
        SQLiteDatabase sdb = db.getWritableDatabase();
        sdb.execSQL(Query);
        //sdb.close();

    }

    public boolean drop(Context context) {
        Database db = new Database(context);
        SQLiteDatabase sdb = db.getWritableDatabase();
        InputStream ins = context.getResources().openRawResource(R.raw.drop_db);
        BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
        try {
            String line = null;
            String sql = "";
            while ((line = reader.readLine()) != null) {
                sql += line;
            }
            ins.close();
//            runInsert(sdb, sql);
        } catch (IOException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
        //db.close();
        //sdb.close();
        return true;
    }

    public boolean drop1(Context context) {
        Database db = new Database(context);
        SQLiteDatabase sdb = db.getWritableDatabase();
        InputStream ins = context.getResources()
                .openRawResource(R.raw.drop_db1);
        BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
        try {
            String line = null;
            String sql = "";
            while ((line = reader.readLine()) != null) {
                sql += line;
            }
            ins.close();
//            runInsert(sdb, sql);

        } catch (IOException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
        db.close();
        sdb.close();
        return true;
    }

    public static void deleteDatabase(Context context) {
        try {
            String currentDBPath = "/data/" + "litepos.pegasus_andorid_1.example.com.litepos"
                    + "/databases/" + "LitePOS.db";
            File data = Environment.getDataDirectory();
            File currentDB = new File(data, currentDBPath);
            if (!currentDB.exists()) {
                currentDB.delete();
            }
        } catch (Exception e) {
        }

    }

    public int executeDML(String strSQL, SQLiteDatabase database) {
        int i = 0;
        try {
            database.execSQL(strSQL);
            i = 1;
        } catch (Exception ex) {
        }
        return i;
    }
}
