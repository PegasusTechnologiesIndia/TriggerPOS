package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Neeraj on 1/24/2017.
 */

public class Lite_POS_Device {

    private static String tableName = "Lite_POS_Device";
    private String Id;
    private String Device_Id;
    private String App_Type;
    private String Device_Code;
    private String Device_Name;
    private String Expiry_Date;
    private String Device_Symbol;
    private String Location_Code;
    private String Currency_Symbol;
    private String Decimal_Place;
    private String Currency_Place;

    private Database db;
    private ContentValues value;
    private String lic_customer_license_id;
    private String lic_code;
    private String license_key;
    private String license_type;
    private String Status;

    public Lite_POS_Device(Context context, String Id, String Device_Id,
                           String App_Type, String Device_Code, String Device_Name, String Expiry_Date, String Device_Symbol,
                           String Location_Code, String Currency_Symbol, String Decimal_Place, String Currency_Place,String licensecustomerid,String licensecode,String licensekey,String licensetype,String Status
                       ) {

        db = new Database(context);
        value = new ContentValues();

        this.setId(Id);
        this.setDevice_Id(Device_Id);
        this.setApp_Type(App_Type);
        this.setDevice_Code(Device_Code);
        this.setDevice_Name(Device_Name);
        this.setExpiry_Date(Expiry_Date);
        this.setDevice_Symbol(Device_Symbol);
        this.setLocation_Code(Location_Code);
        this.setCurreny_Symbol(Currency_Symbol);
        this.setDecimal_Place(Decimal_Place);
        this.setCurrency_Place(Currency_Place);
        this.setLic_customer_license_id(licensecustomerid);
        this.setLic_code(licensecode);
        this.setLicense_key(licensekey);
        this.setLicense_type(licensetype);
        this.setStatus(Status);

    }



    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
        value.put("Status", status);
    }

    public String getLic_customer_license_id() {
        return lic_customer_license_id;
    }

    public void setLic_customer_license_id(String lic_customer_license_id) {
        this.lic_customer_license_id = lic_customer_license_id;
        value.put("lic_customer_license_id", lic_customer_license_id);
    }

    public String getLic_code() {
        return lic_code;
    }

    public void setLic_code(String lic_code) {
        this.lic_code = lic_code;
        value.put("lic_code", lic_code);
    }

    public String getLicense_key() {
        return license_key;
    }

    public void setLicense_key(String license_key) {
        this.license_key = license_key;
        value.put("license_key", license_key);
    }

    public String getLicense_type() {
        return license_type;
    }

    public void setLicense_type(String license_type) {
        this.license_type = license_type;
        value.put("license_type", license_type);
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
        value.put("Id", Id);
    }

    public String getDevice_Id() {
        return Device_Id;
    }

    public void setDevice_Id(String device_Id) {
        Device_Id = device_Id;
        value.put("Device_Id", Device_Id);
    }

    public String getApp_Type() {
        return App_Type;
    }

    public void setApp_Type(String app_Type) {
        App_Type = app_Type;
        value.put("App_Type", App_Type);
    }

    public String getDevice_Code() {
        return Device_Code;
    }

    public void setDevice_Code(String device_Code) {
        Device_Code = device_Code;
        value.put("Device_Code", Device_Code);
    }

    public String getDevice_Name() {
        return Device_Name;
    }

    public void setDevice_Name(String device_Name) {
        Device_Name = device_Name;
        value.put("Device_Name", Device_Name);
    }

    public String getExpiry_Date() {
        return Expiry_Date;
    }

    public void setExpiry_Date(String expiry_Date) {
        Expiry_Date = expiry_Date;

        value.put("Expiry_Date", Expiry_Date);
    }

    public String getDevice_Symbol() {
        return Device_Symbol;
    }

    public void setDevice_Symbol(String device_symbol) {
        Device_Symbol = device_symbol;
        value.put("Device_Symbol", Device_Symbol);
    }

    public String getLocation_Code() {
        return Location_Code;
    }

    public void setLocation_Code(String location_code) {
        Location_Code = location_code;
        value.put("Location_Code", Location_Code);
    }

    public String getCurreny_Symbol() {
        return Currency_Symbol;
    }

    public void setCurreny_Symbol(String currency_symbol) {
        Currency_Symbol = currency_symbol;
        value.put("Currency_Symbol", Currency_Symbol);
    }

    public String getDecimal_Place() {
        return Decimal_Place;
    }

    public void setDecimal_Place(String decimal_place) {
        Decimal_Place = decimal_place;
        value.put("Decimal_Place", Decimal_Place);
    }

    public String getCurrency_Place() {
        return Currency_Place;
    }

    public void setCurrency_Place(String currency_place) {
        Currency_Place = currency_place;
        value.put("Currency_Place", Currency_Place);
    }

    public long insertDevice(SQLiteDatabase database) {
        //SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "Id", value);
        //database.close();
        return insert;
    }

    public static long delete_Device(Context context, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

        //sdb.close();
        return 1;
    }

    public long updateDevice(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
        //SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        // sdb.close();
        return insert;
    }

    public static Lite_POS_Device getDevice(Context context, String WhereClasue, SQLiteDatabase database) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Lite_POS_Device master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        try {
            Cursor cursor = database.rawQuery(Query, null);
            if (cursor.moveToFirst()) {
                do {
                    master = new Lite_POS_Device(context, cursor.getString(0),
                            cursor.getString(1), cursor.getString(2),
                            cursor.getString(3), cursor.getString(4),
                            cursor.getString(5), cursor.getString(6),
                            cursor.getString(7), cursor.getString(8),
                            cursor.getString(9), cursor.getString(10),
                            cursor.getString(11),cursor.getString(12),cursor.getString(13),cursor.getString(14),cursor.getString(15));
                } while (cursor.moveToNext());
            }
            cursor.close();
//            database.close();
//            db.close();

        } catch (Exception ex) {
        }

        return master;
    }
}
