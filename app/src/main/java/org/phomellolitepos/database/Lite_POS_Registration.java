package org.phomellolitepos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import org.phomellolitepos.database.Database;

import java.util.ArrayList;

/**
 * Created by Neeraj on 1/24/2017.
 */

public class Lite_POS_Registration {

    private static String tableName = "Lite_POS_Registration";
    private String Id;
    private String Company_Name;
    private String Contact_Person;
    private String Mobile_No;
    private String Country_Id;
    private String Zone_Id;
    private String Password;
    private String License_No;
    private String Email;
    private String Address;
    private String company_id;
    private String project_id;
    private String Registration_Code;
    private String Service_code_tariff;
    private String Industry_Type;
    private String Short_companyname;
    private String Country_Name;
    private String Zone_Name;
private String Country_name;
        private String Zone_name;

    private Database db;
    private ContentValues value;

    public Lite_POS_Registration(Context context, String Id, String Company_Name,
                                 String Contact_Person, String Mobile_No, String Country_Id, String Zone_Id, String Password,
                                 String License_No, String Email, String Address, String company_id,String project_id,String Registration_Code,String Service_code_tariff,String Industry_Type,String shortcompanyname,
                                 String countryname,String zonename) {

        db = new Database(context);
        value = new ContentValues();

        this.setId(Id);
        this.setCompany_Name(Company_Name);
        this.setContact_Person(Contact_Person);
        this.setMobile_No(Mobile_No);
        this.setCountry_Id(Country_Id);
        this.setZone_Id(Zone_Id);
        this.setPassword(Password);
        this.setLicense_No(License_No);
        this.setEmail(Email);
        this.setAddress(Address);
        this.setcompany_id(company_id);
        this.setproject_id(project_id);
        this.setRegistration_Code(Registration_Code);
        this.setService_code_tariff(Service_code_tariff);
        this.setIndustry_Type(Industry_Type);
        this.setShort_companyname(shortcompanyname);
        this.setCountry_name(countryname);
        this.setZone_name(zonename);
    }

    public String getCountry_name() {
        return Country_name;
    }

    public void setCountry_name(String country_name) {
        Country_name = country_name;
        value.put("Country_name", Country_name);
    }

    public String getZone_name() {
        return Zone_name;
    }

    public void setZone_name(String zone_name) {
        Zone_name = zone_name;
        value.put("Zone_name", Zone_name);
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
        value.put("Id", Id);
    }

    public String getShort_companyname() {
        return Short_companyname;
    }

    public void setShort_companyname(String short_companyname) {
        Short_companyname = short_companyname;
        value.put("Short_companyname", Short_companyname);
    }

    public String getCompany_Name() {
        return Company_Name;
    }

    public void setCompany_Name(String company_Name) {
        Company_Name = company_Name;
        value.put("Company_Name", Company_Name);
    }

    public String getContact_Person() {
        return Contact_Person;

    }

    public void setContact_Person(String contact_Person) {
        Contact_Person = contact_Person;
        value.put("Contact_Person", Contact_Person);
    }

    public String getMobile_No() {
        return Mobile_No;
    }

    public void setMobile_No(String mobile_No) {
        Mobile_No = mobile_No;
        value.put("Mobile_No", Mobile_No);
    }

    public String getCountry_Id() {
        return Country_Id;
    }

    public void setCountry_Id(String country_Id) {
        Country_Id = country_Id;
        value.put("Country_Id", Country_Id);
    }

    public String getZone_Id() {
        return Zone_Id;
    }

    public void setZone_Id(String zone_Id) {
        Zone_Id = zone_Id;
        value.put("Zone_Id", Zone_Id);
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
        value.put("Password", Password);
    }

    public String getLicense_No() {
        return License_No;
    }

    public void setLicense_No(String license_No) {
        License_No = license_No;
        value.put("License_No", License_No);
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
        value.put("Email", Email);
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
        value.put("Address", Address);
    }

    public String getcompany_id() {
        return company_id;
    }

    public void setcompany_id(String Company_id) {
        company_id = Company_id;
        value.put("company_id", company_id);
    }

    public String getproject_id() {
        return project_id;
    }

    public void setproject_id(String Project_id) {
        project_id = Project_id;
        value.put("project_id", project_id);
    }

    public String getRegistration_Code() {
        return Registration_Code;
    }

    public void setRegistration_Code(String registration_code) {
        Registration_Code = registration_code;
        value.put("Registration_Code", Registration_Code);
    }

    public String getService_code_tariff() {
        return Service_code_tariff;
    }

    public void setService_code_tariff(String service_code_tariff) {
        Service_code_tariff = service_code_tariff;
        value.put("Service_code_tariff", Service_code_tariff);
    }

    public String getIndustry_Type() {
        return Industry_Type;
    }

    public void setIndustry_Type(String industry_type) {
        Industry_Type = industry_type;
        value.put("Industry_Type", Industry_Type);
    }

    public long insertRegistration(SQLiteDatabase database) {
        //SQLiteDatabase database = db.getWritableDatabase();
        long insert = database.insert(tableName, "Id", value);
        //database.close();
        return insert;
    }

    public static long delete_Registration(Context context, String lite_pos_registration, String whereClause, String[] whereArgs, SQLiteDatabase database) {
//        Database db = new Database(context);
//        SQLiteDatabase sdb = db.getWritableDatabase();
        database.delete(tableName, whereClause, whereArgs);

       // sdb.close();
        return 1;
    }

    public long updateRegistration(String whereClause, String[] whereArgs, SQLiteDatabase database)
            throws SQLiteConstraintException {
//        SQLiteDatabase sdb = db.getWritableDatabase();
        long insert = database.updateWithOnConflict(tableName, value, whereClause,
                whereArgs, SQLiteDatabase.CONFLICT_FAIL);
        //sdb.close();
        return insert;
    }

    public long delete_register(Context context, String whereClause, String[] whereArgs)
            throws SQLiteConstraintException {
        SQLiteDatabase sdb = db.getWritableDatabase();
        long delete = sdb.delete(tableName, whereClause, whereArgs);

       // sdb.close();
        return delete;
    }

    public static Lite_POS_Registration getRegistration(Context context, SQLiteDatabase database, Database db, String WhereClasue) {
        String Query = "Select * FROM " + tableName + " " + WhereClasue;
        Lite_POS_Registration master = null;
//        Database db = new Database(context);
//        SQLiteDatabase database = db.getReadableDatabase();
        try {
            Cursor cursor = database.rawQuery(Query, null);
            if (cursor.moveToFirst()) {
                do {
                    master = new Lite_POS_Registration(context, cursor.getString(0),
                            cursor.getString(1), cursor.getString(2),
                            cursor.getString(3), cursor.getString(4),
                            cursor.getString(5), cursor.getString(6),
                            cursor.getString(7), cursor.getString(8),
                            cursor.getString(9),cursor.getString(10),
                            cursor.getString(11), cursor.getString(12),
                            cursor.getString(13),
                            cursor.getString(14), cursor.getString(15),cursor.getString(16),cursor.getString(17));
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
