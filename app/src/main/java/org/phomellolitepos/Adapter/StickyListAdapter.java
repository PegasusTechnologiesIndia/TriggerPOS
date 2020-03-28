package org.phomellolitepos.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import org.phomellolitepos.DataBaseActivity;
import org.phomellolitepos.MainActivity;

import org.phomellolitepos.R;
import org.phomellolitepos.ReceptActivity;
import org.phomellolitepos.ReceptDetailActivity;
import org.phomellolitepos.StickyList.StickyListHeadersAdapter;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Acc_Customer;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Orders;
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
        try {
            line_total = Globals.myNumberFormat2Price(Double.parseDouble(resultp.get_total()), decimal_check);
        } catch (Exception ex) {
            line_total = "0";
        }
        holder.txt_table_code.setText(resultp.get_table_code());
        holder.txt_line_total.setText(line_total);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (resultp.get_order_status().equals("CLOSE")) {
                    Orders resultp = result_list.get(position);
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
        String strheaderQry = "select count(*),SUM(total) from orders where DATE(order_date)  = '" + result_list.get(position).get_order_date().subSequence(0, 10) + "' And is_active = '1' And z_code ='0'";
        Cursor cursor = database.rawQuery(strheaderQry, null);
        String headerTotal = "";
        while (cursor.moveToNext()) {
            String header_total;
            header_total = Globals.myNumberFormat2Price(Double.parseDouble(cursor.getString(1)), decimal_check);
            headerTotal = header_total + " (" + cursor.getString(0) + ")";
        }
        cursor.close();
        CharSequence headerChar = result_list.get(position).get_order_date().subSequence(0, 10);
        holder.text1.setText(headerChar);
        holder.text2.setText(headerTotal);
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

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                activity);
        alertDialog.setTitle("Orders");
        alertDialog
                .setMessage("Select operation");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        if (strOrderStatus.equals("CLOSE")) {
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
                                Intent intent = new Intent(mContext, MainActivity.class);
                                intent.putExtra("order_code", strOrderCode);
                                intent.putExtra("opr", strOperation);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(intent);
                            }
                        }
                    });
        }

        alertDialog.setPositiveButton("Cancel Order",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        String str = "13245";

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
                        final String finalStr = str;
                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (edt_pass.getText().toString().trim().equals("")) {
                                    edt_pass.setError("Password is required");
                                    return;
                                }

                                if (finalStr.equals(edt_pass.getText().toString().trim())) {
                                    listDialog2.dismiss();
                                    Orders orders = Orders.getOrders(mContext, database, " WHERE order_code = '" + strOrderCode + "'");
                                    orders.set_order_status("CANCEL");
                                    long l = orders.updateOrders("order_code=?", new String[]{strOrderCode}, database);

                                    if (l > 0) {
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

                                        Toast.makeText(mContext, "Order has beed canceled", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(mContext, ReceptActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mContext.startActivity(intent);
                                    }
                                } else {
                                    Toast.makeText(activity, R.string.password_is_wrong, Toast.LENGTH_SHORT).show();
                                }
                            }


                        });
                    }
                });

        alertDialog.setNeutralButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
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

