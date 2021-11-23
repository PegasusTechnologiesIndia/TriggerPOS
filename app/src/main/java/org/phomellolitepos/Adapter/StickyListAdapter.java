package org.phomellolitepos.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.viewpager.widget.ViewPager;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import org.phomellolitepos.Main2Activity;
import org.phomellolitepos.MainActivity;

import org.phomellolitepos.R;
import org.phomellolitepos.ReceptActivity;
import org.phomellolitepos.ReceptDetailActivity;
import org.phomellolitepos.Retail_IndustryActivity;
import org.phomellolitepos.StickyList.StickyListHeadersAdapter;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Acc_Customer;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Order_Payment;
import org.phomellolitepos.database.Orders;
import org.phomellolitepos.database.Payment;
import org.phomellolitepos.database.Table;
import org.phomellolitepos.database.Unit;
import org.phomellolitepos.database.User;

public class StickyListAdapter extends BaseAdapter implements
        StickyListHeadersAdapter, SectionIndexer {
    private Context mContext;
    private int[] mSectionIndices;
    private String[] mSectionLetters;
    private LayoutInflater mInflater;
    ArrayList<Orders> result_list;
    Database db;
    String decimal_check;
    SQLiteDatabase database;
    private Activity activity;
    ArrayList<Payment> paymentArrayList;
    String Strpassword="";
    String PayId,strPayMethod;
    public StickyListAdapter(Context context, ArrayList<Orders> arrayList, ReceptActivity receptActivity) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        result_list = arrayList;
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
        activity = receptActivity;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private int[] getSectionIndices() {
        ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
        String lastFirstChar = result_list.get(0).get_order_date();
        lastFirstChar = lastFirstChar.substring(0, 10);
        sectionIndices.add(0);

        for (int i = 0; i < result_list.size(); i++) {
            if (!result_list.get(i).get_order_date().contains(lastFirstChar)) {
                lastFirstChar = result_list.get(i).get_order_date();
                lastFirstChar = lastFirstChar.substring(0, 10);
                sectionIndices.add(i);
            }
        }
        int[] sections = new int[sectionIndices.size()];

        for (int i = 0; i < sectionIndices.size(); i++) {
            sections[i] = sectionIndices.get(i);
        }
        return sections;
    }

    private String[] getSectionLetters() {
        String[] letters = new String[mSectionIndices.length];
        for (int i = 0; i < mSectionIndices.length; i++) {
            String abc = result_list.get(mSectionIndices[i]).get_order_date();
            abc = abc.substring(0, 10);
            letters[i] = abc;
        }
        return letters;
    }

    @Override
    public int getCount() {
        return result_list.size();
    }

    @Override
    public Object getItem(int position) {
        return result_list.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.sticky_list_row, parent, false);
            holder.txt_order_code = (TextView) convertView.findViewById(R.id.txt_order_code);
            holder.txt_time = (TextView) convertView.findViewById(R.id.txt_time);
            holder.txt_line_total = (TextView) convertView.findViewById(R.id.txt_line_total);
            holder.image_recept = (ImageView) convertView.findViewById(R.id.image_recept);
            holder.txt_table_code = (TextView) convertView.findViewById(R.id.txt_table_code);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        db = new Database(mContext);
        database = db.getWritableDatabase();
        final Orders resultp = result_list.get(position);

        if (resultp.get_order_status().equals("CLOSE")) {

            holder.image_recept.setBackgroundResource(R.drawable.post);

        } else if (resultp.get_order_status().equals("CANCEL")) {
            holder.image_recept.setBackgroundResource(R.drawable.cancel);
        } else {
            holder.image_recept.setBackgroundResource(R.drawable.list);
        }

        holder.txt_order_code.setText(resultp.get_order_code());
        if (resultp.get_remarks().equals("0") || resultp.get_remarks().equals("")) {
            holder.txt_time.setText("");
        } else {
            holder.txt_time.setText("( " + resultp.get_remarks() + " )");
        }
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
        } catch (Exception ex) {
            decimal_check = "1";
        }
        String line_total;
       // if(!resultp.get_order_status().equals("CANCEL")) {

            try {
                line_total = Globals.myNumberFormat2Price(Double.parseDouble(resultp.get_total()), decimal_check);
            } catch (Exception ex) {
                line_total = "0";
            }
            holder.txt_line_total.setText(line_total);
       // }
        Table table = Table.getTable(mContext, database, db, " WHERE table_code='" + resultp.get_table_code() + "'");
if(table!=null) {
    holder.txt_table_code.setText(table.get_table_name());
}
else{

}
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (resultp.get_order_status().equals("CLOSE")) {

                    String strOrderCode = resultp.get_order_code();
                    Intent intent = new Intent(mContext, ReceptDetailActivity.class);
                    intent.putExtra("order_code", strOrderCode);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);

                } else if (resultp.get_order_status().equals("CANCEL")) {
                    Toast.makeText(mContext, "Cancel order can not be opened!", Toast.LENGTH_SHORT).show();
                } else {

                }

            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (resultp.get_order_status().equals("CANCEL")) {
                    Toast.makeText(mContext, "Cancel order can not be opened!", Toast.LENGTH_SHORT).show();
                } else {

                    if (resultp.get_z_code().equals("0")) {
                        Orders resultp = result_list.get(position);

                        String strOrderCode = resultp.get_order_code();
                        String strOrderStatus = resultp.get_order_status();
                        String strContactCode = resultp.get_contact_code();
                        dialog_for_edit(strOrderCode, strOrderStatus, strContactCode);
                    }
                }

                return false;
            }

        });

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.sticky_list_header, parent, false);
            holder.text1 = (TextView) convertView.findViewById(R.id.text1);
            holder.text2 = (TextView) convertView.findViewById(R.id.text2);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        String strheaderQry = "select count(*),SUM(total) from orders where DATE(order_date)  = '" + result_list.get(position).get_order_date().subSequence(0, 10) + "' And is_active = '1' And z_code ='0' AND order_status!='CANCEL'";
        Cursor cursor = database.rawQuery(strheaderQry, null);
        String headerTotal = "";
        while (cursor.moveToNext()) {
            String header_total="";
            try {
                header_total = Globals.myNumberFormat2Price(Double.parseDouble(cursor.getString(1)), decimal_check);
                if(header_total!=null) {
                    headerTotal = header_total + " (" + cursor.getString(0) + ")";
                    holder.text2.setText(headerTotal);
                }
            }catch(Exception e){

            }
        }
        cursor.close();
        CharSequence headerChar = result_list.get(position).get_order_date().subSequence(0, 10);
        holder.text1.setText(headerChar);


        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        int i = 0;
        String date = result_list.get(position).get_order_date().substring(0, 10);

        for (int j = 0; j < mSectionLetters.length; j++) {
            if (mSectionLetters[j].equals(date))
                i = j;
        }
        return i;
    }

    @Override
    public int getPositionForSection(int section) {
        if (mSectionIndices.length == 0) {
            return 0;
        }

        if (section >= mSectionIndices.length) {
            section = mSectionIndices.length - 1;
        } else if (section < 0) {
            section = 0;
        }
        return mSectionIndices[section];
    }

    @Override
    public int getSectionForPosition(int position) {
        for (int i = 0; i < mSectionIndices.length; i++) {
            if (position < mSectionIndices[i]) {
                return i - 1;
            }
        }
        return mSectionIndices.length - 1;
    }

    @Override
    public Object[] getSections() {
        return mSectionLetters;
    }

    private class HeaderViewHolder {
        TextView text1;
        TextView text2;
    }

    private class ViewHolder {
        TextView txt_time;
        TextView txt_table_code;
        TextView txt_order_code;
        TextView txt_line_total;
        ImageView image_recept;

    }

    private void dialog_for_edit(final String strOrderCode, String strOrderStatus, final String strContactCode) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                activity);
        alertDialog.setTitle(R.string.cancelorder);
        alertDialog
                .setMessage(R.string.cancelordermsg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        if (strOrderStatus.equals("CLOSE")) {
            if(Globals.objsettings.getIs_paymentmethod().equals("true")) {
                alertDialog.setNegativeButton("Change Payment Type",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (Globals.cart.size() > 0) {
                                    Toast.makeText(activity, "Cart should be clear for edit any order,Go back to POS screen", Toast.LENGTH_SHORT).show();
                                } else {

                                    Dialog listDialog1 = new Dialog(activity);
                                    listDialog1.setTitle(R.string.Select_paymentmethod);
                                    LayoutInflater li1 = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View v1 = li1.inflate(R.layout.custom_payment_layout, null, false);
                                    listDialog1.setContentView(v1);
                                    listDialog1.setCancelable(true);

                                    final TextView tv_paymentname = (TextView) listDialog1.findViewById(R.id.txt_paymentname);
                                    final Spinner spn_payment = (Spinner) listDialog1.findViewById(R.id.spin_paymenttype);
                                    Button btnsave = (Button) listDialog1.findViewById(R.id.btn_save);


                                    listDialog1.show();

                                    Window window = listDialog1.getWindow();
                                    window.setLayout(500, 300);
                                    String name = "";
                                    ArrayList<Order_Payment> order_payment_array = Order_Payment.getAllOrder_Payment(activity, " where order_code='" + strOrderCode + "'");
                                    if (order_payment_array.size() > 0) {
                                        for (int i = 0; i < order_payment_array.size(); i++) {
                                            Payment payment = Payment.getPayment(activity, " where payment_id = '" + order_payment_array.get(i).get_payment_id() + "'");

                                            if (payment != null) {
                                                name = payment.get_payment_name();
                                                tv_paymentname.setText("OrderPayment Type Selected : " + name);

                                            }

                                        }
                                    }

                                    if (Globals.strContact_Code.equals("")) {
                                        paymentArrayList = Payment.getAllPayment(activity, " WHERE is_active ='1'  and payment_id!=3");
                                    } else {
                                        paymentArrayList = Payment.getAllPayment(activity, " WHERE is_active ='1'");
                                    }

                                    PaymentListAdapter paymentListAdapter = new PaymentListAdapter(mContext, paymentArrayList);
                                    spn_payment.setAdapter(paymentListAdapter);

                                    if (!name.equals("")) {
                                        for (int i = 0; i < paymentListAdapter.getCount(); i++) {
                                            String iname = paymentArrayList.get(i).get_payment_name();
                                            if (name.equals(iname)) {
                                                spn_payment.setSelection(i);
                                                System.out.println("selection"+i);
                                                break;
                                            }
                                        }
                                    }
                                    spn_payment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                            Payment resultp = paymentArrayList.get(position);
                                            strPayMethod = resultp.get_payment_name();
                                            try {
                                               PayId = resultp.get_payment_id();
                                                System.out.println("Paymentid" + PayId);
                                            } catch (Exception ex) {
                                                PayId = "";
                                            }

                                            if (PayId.equals("5")) {
                                                if (Globals.strContact_Code.equals("")) {
                                                    Toast.makeText(activity, "Please select customer for this mode", Toast.LENGTH_SHORT).show();

                                                }
                                            }

                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });
                                    btnsave.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            Order_Payment orderpayment = Order_Payment.getOrder_Payment(activity, "where order_code='"+strOrderCode+"'", database);
                                            orderpayment.set_payment_id(PayId);
                                            long ct = orderpayment.updateOrder_Payment("order_code=?", new String[]{strOrderCode}, database);
                                            if (ct > 0) {
                                                Toast.makeText(activity, "update successfully", Toast.LENGTH_SHORT).show();

                                            }

                                            listDialog1.dismiss();
                                        }
                                    });


                                }
                            }
                        }
                );
            }
            else{

            }
        } else {
            alertDialog.setNegativeButton("Edit",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            if (Globals.cart.size() > 0) {
                                Toast.makeText(mContext, "Cart should be clear for edit any order,Go back to POS screen", Toast.LENGTH_SHORT).show();
                            } else {
                                Globals.CheckContact = "1";
                                String strOperation = "Edit";
                                if(Globals.objLPR.getIndustry_Type().equals("1")) {
                                    if (Globals.objsettings.get_Home_Layout().equals("0")) {
                                        Intent intent = new Intent(mContext, MainActivity.class);
                                        intent.putExtra("order_code", strOrderCode);
                                        intent.putExtra("opr", strOperation);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mContext.startActivity(intent);
                                    }
                                    else if(Globals.objsettings.get_Home_Layout().equals("1")){
                                        Intent intent = new Intent(mContext, Main2Activity.class);
                                        intent.putExtra("order_code", strOrderCode);
                                        intent.putExtra("opr", strOperation);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mContext.startActivity(intent);
                                    }
                                }
                                else if(Globals.objLPR.getIndustry_Type().equals("2")){
                                    Intent intent = new Intent(mContext, Retail_IndustryActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("order_code", strOrderCode);
                                    intent.putExtra("opr", strOperation);
                                    mContext.startActivity(intent);
                                }
                            }
                        }
                    });
        }

        alertDialog.setPositiveButton(R.string.alert_posbtn,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {


                        final Dialog listDialog2 = new Dialog(activity);
                        LayoutInflater li1 = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View v1 = li1.inflate(R.layout.password_dialog, null, false);
                        listDialog2.setContentView(v1);
                        listDialog2.setCancelable(true);
                        final EditText edt_pass = (EditText) listDialog2.findViewById(R.id.edt_pass);
                        Button btn_ok = (Button) listDialog2.findViewById(R.id.btn_ok);
                        listDialog2.show();
                        Window window = listDialog2.getWindow();
                        window.setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
                        edt_pass.setText("");

                        try {
                            User user = User.getUser(mContext, " Where user_code='" + Globals.user + "'", database);
                             Strpassword = user.get_password();
                        }
                        catch (Exception e){

                        }

                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    if (edt_pass.getText().toString().trim().equals("")) {
                                        edt_pass.setError("Password is required");
                                        return;
                                    }

                                    if (Strpassword.equals(edt_pass.getText().toString().trim())) {
                                        listDialog2.dismiss();
                                        Orders orders = Orders.getOrders(mContext, database, " WHERE order_code = '" + strOrderCode + "'");
                                        orders.set_order_status("CANCEL");
                                        orders.set_is_push("N");
                                        long l = orders.updateOrders("order_code=?", new String[]{strOrderCode}, database);

                                        if (l > 0) {
                                            try {
                                                Double strOldBalance = 0d;
                                                Double strAmount = 0d;
                                                Acc_Customer acc_customer = Acc_Customer.getAcc_Customer(mContext, " where contact_code='" + strContactCode + "'", database);
                                                if (acc_customer == null) {
                                                    strAmount = strOldBalance + Double.parseDouble(orders.get_total());
                                                    acc_customer = new Acc_Customer(mContext, null, strContactCode, strAmount + "");
                                                    acc_customer.insertAcc_Customer(database);
                                                } else {
                                                    strOldBalance = Double.parseDouble(acc_customer.get_amount());
                                                    strAmount = strOldBalance + Double.parseDouble(orders.get_total());
                                                    acc_customer.set_amount(strAmount + "");
                                                    long a = acc_customer.updateAcc_Customer("contact_code=?", new String[]{strContactCode}, database);
                                                }
                                                if (Globals.objLPR.getproject_id().equals("cloud") && Globals.objsettings.get_IsOnline().equals("true")) {
                                                    Intent intent = new Intent(mContext, ReceptActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    intent.putExtra("cancelflag","CANCEL");
                                                    mContext.startActivity(intent);
                                                }
                                                else {
                                                    Toast.makeText(mContext, "Order has been canceled", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(mContext, ReceptActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                                    mContext.startActivity(intent);
                                                }
                                            } catch (Exception e) {
                                                System.out.println(e.getMessage());
                                            }
                                        }
                                    } else {
                                        Toast.makeText(activity, R.string.password_is_wrong, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                catch(Exception e){

                                }
                            }

                        });
                    }
                });

        alertDialog.setNeutralButton(R.string.alert_nobtn,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                       dialog.dismiss();
                    }

                });

        AlertDialog alert = alertDialog.create();
        alert.show();
        Button ngbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        ngbutton.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));

        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));

        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
        nbutton.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));

    }


}

