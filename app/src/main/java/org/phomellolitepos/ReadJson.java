package org.phomellolitepos;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;

import java.io.IOException;

import java.io.InputStreamReader;

import org.phomellolitepos.database.*;
import org.phomellolitepos.database.Country;
import org.phomellolitepos.database.Zone;


public class ReadJson extends AppCompatActivity {

    org.phomellolitepos.database.Country country;
    Address_Category address_category;
    Address_Type address_type;
    Payment payment;
    Bussiness_Group bussiness_group;
    org.phomellolitepos.database.Zone zone;
    Order_Type order_type;
    SQLiteDatabase database;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_json);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        read_country();
        read_zone();
        read_defult();

    }

    private void read_defult() {

        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.defaullt)));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close(); // stop reading
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {

                try {
                    long l = Address_Category.delete_Address_Category(getApplicationContext(), null, null, database);
                    if (l > 0) {
                    } else {
                    }
                } catch (Exception ex) {
                }

                JSONObject jsonObject_default = new JSONObject(sb.toString());

                JSONArray jsonArray_address_category = jsonObject_default.getJSONArray("address_category");

                for (int i = 0; i < jsonArray_address_category.length(); i++) {

                    JSONObject jsonObject_address_category1 = jsonArray_address_category.getJSONObject(i);

                    address_category = new Address_Category(getApplicationContext(), null,
                            jsonObject_address_category1.getString("device_code"),
                            jsonObject_address_category1.getString("address_category_code"),
                            jsonObject_address_category1.getString("name"),
                            jsonObject_address_category1.getString("is_active"),
                            jsonObject_address_category1.getString("modified_by"),
                            jsonObject_address_category1.getString("modified_date"), "N");

                    //address_category.insertAddress_Category(db);
                }

                try {
                    long l = Address_Type.delete_Address_Type(getApplicationContext(), null, null, database);
                    if (l > 0) {
                    } else {
                    }
                } catch (Exception ex) {
                }

                JSONArray jsonArray_address_type = jsonObject_default.getJSONArray("address_type");

                for (int i = 0; i < jsonArray_address_type.length(); i++) {

                    JSONObject jsonObject_address_type1 = jsonArray_address_type.getJSONObject(i);

                    address_type = new Address_Type(getApplicationContext(), null,
                            jsonObject_address_type1.getString("name"));

                    //address_type.insertAddress_Type(db);
                }


                try {
                    long l = Bussiness_Group.delete_Bussiness_Group(getApplicationContext(), null, null, database);
                    if (l > 0) {
                    } else {
                    }
                } catch (Exception ex) {
                }

                JSONArray jsonArray_bussiness_group = jsonObject_default.getJSONArray("business_group");

                for (int i = 0; i < jsonArray_bussiness_group.length(); i++) {

                    JSONObject jsonObject_bussiness_group1 = jsonArray_bussiness_group.getJSONObject(i);

//                    bussiness_group = new Bussiness_Group(getApplicationContext(), null
//                            , jsonObject_bussiness_group1.getString("device_code"),
//                            jsonObject_bussiness_group1.getString("business_group_code"),
//                            jsonObject_bussiness_group1.getString("name"),
//                            jsonObject_bussiness_group1.getString("is_active"),
//                            jsonObject_bussiness_group1.getString("modified_by"), jsonObject_bussiness_group1.getString("modified_date"), "N");

                    //bussiness_group.insertBussiness_Group(database);
                }

                try {
                    long l = Order_Type.delete_Order_Type(getApplicationContext(), null, null, database);
                    if (l > 0) {
                    } else {
                    }
                } catch (Exception ex) {
                }

                JSONArray jsonArray_order_type = jsonObject_default.getJSONArray("order_type");

                for (int i = 0; i < jsonArray_order_type.length(); i++) {

                    JSONObject jsonObject_order_type1 = jsonArray_order_type.getJSONObject(i);

                    order_type = new Order_Type(getApplicationContext(), jsonObject_order_type1.getString("order_type_id")
                            , jsonObject_order_type1.getString("name"),
                            jsonObject_order_type1.getString("is_active"),
                            jsonObject_order_type1.getString("modified_by"),
                            jsonObject_order_type1.getString("modified_date"), "N");

                   // order_type.insertOrder_Type(db);
                }

                try {
                    long l = Payment.delete_Payment(getApplicationContext(),"payments", null, null, database);
                    if (l > 0) {
                    } else {
                    }
                } catch (Exception ex) {
                }

                JSONArray jsonArray_payments = jsonObject_default.getJSONArray("payments");

                for (int i = 0; i < jsonArray_payments.length(); i++) {

                    JSONObject jsonObject_payments1 = jsonArray_payments.getJSONObject(i);

                    payment = new Payment(getApplicationContext(), jsonObject_payments1.getString("payment_id")
                            , jsonObject_payments1.getString("parent_id"),
                            jsonObject_payments1.getString("payment_name"),
                            jsonObject_payments1.getString("is_active"),
                            jsonObject_payments1.getString("modified_by"),
                            jsonObject_payments1.getString("modified_date"), "N");

                    //payment.insertPayment(db);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void read_zone() {


        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.zone)));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close(); // stop reading
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {

                try {
                    long l = Zone.delete_Zone(getApplicationContext(), null, null, database);
                    if (l > 0) {
                    } else {
                    }
                } catch (Exception ex) {
                }

                JSONObject jsonObject_zone = new JSONObject(sb.toString());

                JSONArray jsonArray_zone = jsonObject_zone.getJSONArray("zone");

                for (int i = 0; i < jsonArray_zone.length(); i++) {

                    JSONObject jsonObject_zone1 = jsonArray_zone.getJSONObject(i);

                    zone = new Zone(getApplicationContext(), jsonObject_zone1.getString("zone_id"),
                            jsonObject_zone1.getString("country_id"), jsonObject_zone1.getString("name"), jsonObject_zone1.getString("code"), jsonObject_zone1.getString("status"));

                    //zone.insertZone(db);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void read_country() {


        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.country)));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close(); // stop reading
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {


                try {
                    long l = Country.delete_country(getApplicationContext(), null, null, database);
                    if (l > 0) {
                    } else {
                    }
                } catch (Exception ex) {
                }

                JSONObject jsonObject_country = new JSONObject(sb.toString());

                JSONArray jsonArray_country = jsonObject_country.getJSONArray("country");

                for (int i = 0; i < jsonArray_country.length(); i++) {

                    JSONObject jsonObject_country1 = jsonArray_country.getJSONObject(i);

                    country = new Country(getApplicationContext(), jsonObject_country1.getString("country_id"),
                            jsonObject_country1.getString("name"), jsonObject_country1.getString("isd_code"), jsonObject_country1.getString("iso_code_2"), jsonObject_country1.getString("iso_code_3"), jsonObject_country1.getString("currency_symbol"), jsonObject_country1.getString("currency_place"), jsonObject_country1.getString("decimal_place"), jsonObject_country1.getString("postcode_required"), jsonObject_country1.getString("status"));

                   // country.insertCountry(db);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}
