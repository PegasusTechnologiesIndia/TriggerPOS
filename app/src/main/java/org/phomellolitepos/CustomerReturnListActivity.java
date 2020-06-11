package org.phomellolitepos;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.phomellolitepos.Adapter.CustomerReturnListAdapter;
import org.phomellolitepos.Mail.GMailSender;
import org.phomellolitepos.Util.DateUtill;
import org.phomellolitepos.Util.ExceptionHandler;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.HeaderAndFooter;
import org.phomellolitepos.Util.Watermark;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Return_detail;
import org.phomellolitepos.database.Returns;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.Unit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

public class CustomerReturnListActivity extends AppCompatActivity {
    EditText edt_toolbar_item_list;
    TextView item_title;
    ArrayList<Returns> arrayList;
    Returns returns;
    CustomerReturnListAdapter customerReturnListAdapter;
    ProgressDialog pDialog;
    Database db;
    SQLiteDatabase database;
    Dialog listDialog;
    Settings settings;
    String decimal_check, qty_decimal_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new Database(getApplicationContext());
        database = db.getWritableDatabase();
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        getSupportActionBar().setTitle("");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_MULTI_PROCESS); // 0 - for private mode
        int id = pref.getInt("id", 0);
        if (id == 0) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp_mdpi);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_forward_black_24dp);
        }

        listDialog = new Dialog(this);
        settings = Settings.getSettings(getApplicationContext(), database, "");
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
            qty_decimal_check = settings.get_Qty_Decimal();
        } catch (Exception ex) {

            decimal_check = "1";
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(CustomerReturnListActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.Wait_msg));
                pDialog.show();
                Thread timerThread = new Thread() {
                    public void run() {
                            try {
                                Intent intent = new Intent(CustomerReturnListActivity.this, ReturnOptionActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                pDialog.dismiss();
                                finish();
                            } finally {}
                    }
                };
                timerThread.start();
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        item_title = (TextView) findViewById(R.id.item_title);
        edt_toolbar_item_list = (EditText) findViewById(R.id.edt_toolbar_item_list);

        edt_toolbar_item_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (edt_toolbar_item_list.getText().toString().trim().equals("")) {
                    return false;
                } else {
                    edt_toolbar_item_list.requestFocus();
                    edt_toolbar_item_list.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    InputMethodManager imm4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm4.showSoftInput(edt_toolbar_item_list, InputMethodManager.SHOW_IMPLICIT);
                    edt_toolbar_item_list.selectAll();
                    return true;
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerReturnListActivity.this, CusReturnHeaderActivity.class);
                intent.putExtra("operation", "Add");
                startActivity(intent);
                finish();
            }
        });
        // Gettting item list here
        getStockList("");
    }

    private void getStockList(String strFilter) {
        arrayList = Returns.getAllReturns(getApplicationContext(), "WHERE is_active = '1' and return_type='CR' and z_code='0'" + strFilter + " Order By lower(modified_date) desc limit "+Globals.ListLimit+"", database);
        returns = Returns.getReturns(getApplicationContext(), "", database);

        ListView category_list = (ListView) findViewById(R.id.item_list);
        if (arrayList.size() > 0) {
            customerReturnListAdapter = new CustomerReturnListAdapter(CustomerReturnListActivity.this, arrayList);
            category_list.setVisibility(View.VISIBLE);
            item_title.setVisibility(View.GONE);
            category_list.setAdapter(customerReturnListAdapter);
            category_list.setTextFilterEnabled(true);
            customerReturnListAdapter.notifyDataSetChanged();
        } else {
            category_list.setVisibility(View.GONE);
            item_title.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_send);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item_menu) {
        int id = item_menu.getItemId();
        if (id == R.id.action_settings) {
            String strFilter = edt_toolbar_item_list.getText().toString().trim();
            strFilter = "and (voucher_no Like '%" + strFilter + "%' or remarks Like '%" + strFilter + "%' or date Like '%" + strFilter + "%')";
            edt_toolbar_item_list.selectAll();
            getStockList(strFilter);
            return true;
        }
        return super.onOptionsItemSelected(item_menu);
    }


    public void setView(final String voucher_no) {

        returns = Returns.getReturns(getApplicationContext(), "where voucher_no ='" + voucher_no + "'", database);

        if (returns != null) {
            listDialog.setTitle("Select below action");
            LayoutInflater li1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v1 = li1.inflate(R.layout.add_remarks_dialog, null, false);
            listDialog.setContentView(v1);
            listDialog.setCancelable(true);
            final EditText edt_remark = (EditText) listDialog.findViewById(R.id.edt_remark);
            edt_remark.setHint(R.string.Enter_Email);
            edt_remark.setHintTextColor(Color.parseColor("#cccccc"));
            edt_remark.setVisibility(View.GONE);
            Button btnPDF = (Button) listDialog.findViewById(R.id.btn_save);
            Button btnEXL = (Button) listDialog.findViewById(R.id.btn_clear);
            btnEXL.setVisibility(View.VISIBLE);
            btnPDF.setText("Email");
            btnPDF.setTextColor(Color.parseColor("#ffffff"));
            btnEXL.setText("View");
            btnEXL.setTextColor(Color.parseColor("#ffffff"));
            listDialog.show();

            btnPDF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isNetworkStatusAvialable(getApplicationContext())) {

                        if (settings.get_Is_email().equals("true")) {
                            if (settings.get_Manager_Email().equals("")) {
                            } else {
                                String name = "Return";
                                final String dtt = name + DateUtill.Reportnamedate();
                                String strEmail = settings.get_Manager_Email();
                                performPDFExport(voucher_no, "",dtt);
                                send_email_manager(strEmail, dtt, name);
                            }
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                    }
                    listDialog.dismiss();
                }
            });

            btnEXL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = "Return";
                    final String dtt = name + DateUtill.Reportnamedate();
                    performPDFExport(voucher_no, "view",dtt);
                    listDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            listDialog.dismiss();
        }

    }

    private void send_email_manager(String strEmail, String strFileName, String strReportName) {
        String dtt = Globals.Reportnamedate();
        try {
            String[] recipients = strEmail.split(",");
            final CustomerReturnListActivity.SendEmailAsyncTask email = new CustomerReturnListActivity.SendEmailAsyncTask();
            email.activity = this;

            email.m = new GMailSender(settings.get_Email(), settings.get_Password(), settings.get_Host(), settings.get_Port());
            email.m.set_from(settings.get_Email());
            email.m.setBody(strReportName);
            email.m.set_to(recipients);
            email.m.set_subject(Globals.objLPD.getDevice_Name() + ":" + strReportName + "");
            email.m.addAttachment(Environment.getExternalStorageDirectory().getPath() + "/" + "LitePOS" + "/" + "PDF Report" + "/" + strFileName + ".pdf");


            email.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
        GMailSender m;
        CustomerReturnListActivity activity;

        public SendEmailAsyncTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (m.send()) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Email sent.", Toast.LENGTH_SHORT).show();
                        }
                    });

//                    activity.displayMessage("Email sent.");

                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Email failed to send.", Toast.LENGTH_SHORT).show();
                        }
                    });

//                    activity.displayMessage("Email failed to send.");
                }

                return true;
            } catch (AuthenticationFailedException e) {
                Log.e(ReportViewerActivity.SendEmailAsyncTask.class.getName(), "Bad account details");
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Authentication failed.");
                return false;
            } catch (MessagingException e) {
                Log.e(ReportViewerActivity.SendEmailAsyncTask.class.getName(), "Email failed");
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Email failed to send.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Email failed to send.");
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Unexpected error occured.", Toast.LENGTH_SHORT).show();
                    }
                });

//                activity.displayMessage("Unexpected error occured.");
                return false;
            }
        }
    }

    private boolean isNetworkStatusAvialable(Context applicationContext) {
        // TODO Auto-generated method stub
        ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if (netInfos != null)
                if (netInfos.isConnected())

                    return true;

        }

        return false;
    }


    protected void performPDFExport(String voucher_no, String s,String name) {
        ArrayList<String> list1a, list2a, list3a, list4a, list5a, list6a, list7a, list8a, list9a;

        list1a = new ArrayList<String>();
        list2a = new ArrayList<String>();
        list3a = new ArrayList<String>();
        list4a = new ArrayList<String>();
        list5a = new ArrayList<String>();
        list6a = new ArrayList<String>();
        list7a = new ArrayList<String>();

        list8a = new ArrayList<String>();
        list9a = new ArrayList<String>();

        ArrayList<Return_detail> return_detailArrayList = Return_detail.getAllReturn_detail(getApplicationContext(), "where ref_voucher_no ='" + voucher_no + "'", database);
        int count = 0;
        Double total_qty = 0d;
        Double total_price = 0d;
        Double total_amount = 0d;
        while (count < return_detailArrayList.size()) {
            total_qty = total_qty + Double.parseDouble(return_detailArrayList.get(count).get_qty());
            total_price = total_price + Double.parseDouble(return_detailArrayList.get(count).get_price());
            total_amount = total_amount + Double.parseDouble(return_detailArrayList.get(count).get_line_total());

            String item_code = return_detailArrayList.get(count).get_item_code();
            Item item = Item.getItem(getApplicationContext(), "WHERE item_code = '" + item_code + "'", database, db);
            if (item != null) {
                list3a.add(item.get_item_name());
                Unit unit = Unit.getUnit(getApplicationContext(), database, db, "WHERE unit_id = '" + item.get_unit_id() + "'");
                if (unit != null) {
                    list5a.add(unit.get_name());
                }
            }

            list1a.add(count + 1 + "");
            list2a.add(item_code);
            list4a.add(return_detailArrayList.get(count).get_qty());
            list6a.add(return_detailArrayList.get(count).get_price());
            list7a.add(return_detailArrayList.get(count).get_line_total());

            count++;
        }


        File f = null;
        try {
            File sd = new File(Globals.folder + Globals.pdffolder);
            if (!sd.exists()) {
                sd.mkdirs();
            }
            //String dtt = Globals.Reportnamedate();
            f = new File(Globals.folder + Globals.pdffolder
                    + "/" + name + ".pdf");
            OutputStream file = new FileOutputStream(f);
            Document document = new Document(PageSize.A4);
            document.setMargins(-50f, -50f, 10f, 20f);
            PdfWriter writer = PdfWriter.getInstance(document, file);
            writer.setPageEvent(new Watermark());
            writer.setPageEvent(new HeaderAndFooter(""));
            Image image = null;
            try {
                Bitmap bitmap;
                Uri uri = Uri.parse(settings.get_Logo());
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 30, stream);
                byte[] imageInByte = stream.toByteArray();
                image = Image.getInstance(imageInByte);
                image.scaleAbsolute(42f, 42f);//image width,height

            } catch (Exception ex) {
            }

            Font B12 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
            Font N10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
            Font B10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
            Font N9 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL, BaseColor.BLACK);
            Font N12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);

            PdfPTable tableh = new PdfPTable(1);
            PdfPCell cellh = new PdfPCell(new Paragraph("Return", B12));
            cellh.setColspan(1);
            cellh.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellh.setPadding(5.0f);
            cellh.setBackgroundColor(new BaseColor(204, 204, 204));
            tableh.addCell(cellh);
            document.open();

            PdfPTable company_name = new PdfPTable(1);
            company_name.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            PdfPCell cell1 = new PdfPCell(new Paragraph(Globals.objLPR.getCompany_Name(), B12));
            cell1.setColspan(1);
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setPadding(5.0f);
            company_name.addCell(cell1);
            company_name.setSpacingBefore(5.0f);
            document.open();

            PdfPTable gst_no = new PdfPTable(1);
            gst_no.getDefaultCell().setBorder(Rectangle.ALIGN_CENTER);
            PdfPCell cell2 = new PdfPCell(new Paragraph(Globals.objLPR.getLicense_No(), B12));
            cell2.setColspan(1);
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setPadding(5.0f);
            gst_no.addCell(cell2);
            company_name.setSpacingBefore(5.0f);
            document.open();

            PdfPTable address = new PdfPTable(1);
            address.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            PdfPCell cell3 = new PdfPCell(new Paragraph(Globals.objLPR.getAddress(), B12));
            cell3.setColspan(1);
            cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell3.setBorder(Rectangle.NO_BORDER);
            cell3.setPadding(5.0f);
            address.addCell(cell3);
            company_name.setSpacingBefore(5.0f);
            document.open();

            PdfPTable email = new PdfPTable(1);
            email.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            PdfPCell cell4 = new PdfPCell(new Paragraph("Email : " + Globals.objLPR.getEmail(), B12));
            cell4.setColspan(1);
            cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell4.setBorder(Rectangle.NO_BORDER);
            cell4.setPadding(5.0f);
            email.addCell(cell4);
            company_name.setSpacingBefore(5.0f);
            document.open();

            PdfPTable mobile = new PdfPTable(1);
            mobile.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            PdfPCell cell5 = new PdfPCell(new Paragraph("Mobile No : " + Globals.objLPR.getMobile_No(), B12));
            cell5.setColspan(1);
            cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell5.setBorder(Rectangle.NO_BORDER);
            cell5.setPadding(5.0f);
            mobile.addCell(cell5);
            company_name.setSpacingBefore(5.0f);
            document.open();

            PdfPTable table_ob = new PdfPTable(4);
            Phrase pr = new Phrase("Invoice", B10);
            PdfPCell c1 = new PdfPCell(pr);
            c1.setPadding(5);
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_ob.addCell(c1);

            pr = new Phrase("" + returns.get_voucher_no(), B10);
            PdfPCell c3 = new PdfPCell(pr);
            c3.setPadding(5);
            c3.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_ob.addCell(c3);

            pr = new Phrase("Date", B10);
            PdfPCell c11 = new PdfPCell(pr);
            c11.setPadding(5);
            c11.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_ob.addCell(c11);

            pr = new Phrase("" + returns.get_date().trim().substring(0, 10), B10);
            PdfPCell c32 = new PdfPCell(pr);
            c32.setPadding(5);
            c32.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_ob.addCell(c32);
//            table_cash.setSpacingBefore(10.0f);
            document.open();


            PdfPTable table_cash_total = new PdfPTable(4);

            Phrase pr2 = new Phrase("Location", B10);
            PdfPCell c12 = new PdfPCell(pr2);
            c11.setPadding(5);
            c11.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_cash_total.addCell(c12);

            pr2 = new Phrase("", B10);
            PdfPCell c13 = new PdfPCell(pr2);
            c13.setPadding(5);
            c13.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_cash_total.addCell(c13);
//            table_cash_total.setSpacingBefore(10.0f);


            //PdfPTable table_expenses = new PdfPTable(2);

            Phrase pr21 = new Phrase("Remarks", B10);
            PdfPCell c14 = new PdfPCell(pr21);
            c14.setPadding(5);
            c14.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_cash_total.addCell(c14);
            pr21 = new Phrase("" + returns.get_remarks(), B10);

            PdfPCell c15 = new PdfPCell(pr21);
            c15.setPadding(5);
            c15.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_cash_total.addCell(c15);
            document.open();

            PdfPTable table_total = new PdfPTable(4);

            Phrase pr23 = new Phrase("Supplier Code", B10);
            PdfPCell c16 = new PdfPCell(pr23);
            c16.setPadding(5);
            c16.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_total.addCell(c16);
            pr23 = new Phrase("" + returns.get_contact_code(), B10);

            PdfPCell c17 = new PdfPCell(pr23);
            c17.setPadding(5);
            c17.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_total.addCell(c17);
//            table_total.setSpacingBefore(10.0f);
//            document.open();

//            PdfPTable supplier_name = new PdfPTable(2);

            Phrase pr24 = new Phrase("Supplier Name", B10);
            PdfPCell c18 = new PdfPCell(pr24);
            c18.setPadding(5);
            c18.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_total.addCell(c18);
            Contact contact = Contact.getContact(getApplicationContext(), database, db, " where contact_code='" + returns.get_contact_code() + "'");
            if (contact != null) {
                pr24 = new Phrase("" + returns.get_contact_code(), B10);
                PdfPCell c19 = new PdfPCell(pr24);
                c19.setPadding(5);
                c19.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_total.addCell(c19);
//            table_total.setSpacingBefore(10.0f);

            }
            document.open();
            PdfPTable table = new PdfPTable(7);

            pr = new Phrase("Sr No", B10);
            c1 = new PdfPCell(pr);
            c1.setPadding(5);
            table.addCell(c1);
            pr = new Phrase("Item Code", B10);
            c3 = new PdfPCell(pr);
            c3.setPadding(5);
            c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(c3);
            pr = new Phrase("Item Name", B10);
            PdfPCell c2 = new PdfPCell(pr);
            c2.setPadding(5);
            c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(c2);
            pr = new Phrase("Quantity", B10);
            c3 = new PdfPCell(pr);
            c3.setPadding(5);
            c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(c3);

            pr = new Phrase("Unit", B10);
            c3 = new PdfPCell(pr);
            c3.setPadding(5);
            c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(c3);

            pr = new Phrase("Price/Unit", B10);
            c3 = new PdfPCell(pr);
            c3.setPadding(5);
            c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(c3);
            pr = new Phrase("Amount", B10);
            c3 = new PdfPCell(pr);
            c3.setPadding(5);
            c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(c3);

            for (int i = 0; i < list1a.size(); i++) {
                pr = new Phrase(list1a.get(i), N9);
                PdfPCell c7 = new PdfPCell(pr);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(c7);

                pr = new Phrase(list2a.get(i), N9);
                c7 = new PdfPCell(pr);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c7);

                pr = new Phrase(list3a.get(i), N9);
                c7 = new PdfPCell(pr);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c7);

                pr = new Phrase(Globals.myNumberFormat2QtyDecimal(Double.parseDouble(list4a.get(i)), qty_decimal_check), N9);
                c7 = new PdfPCell(pr);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c7);

                pr = new Phrase(list5a.get(i), N9);
                c7 = new PdfPCell(pr);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c7);

                pr = new Phrase(Globals.myNumberFormat2Price(Double.parseDouble(list6a.get(i)), decimal_check), N9);
                c7 = new PdfPCell(pr);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c7);

                pr = new Phrase(Globals.myNumberFormat2Price(Double.parseDouble(list7a.get(i)), decimal_check), N9);
                c7 = new PdfPCell(pr);
                c7.setPadding(5);
                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c7);
            }
            table.setSpacingBefore(10.0f);
            table.setHeaderRows(1);
            document.open();

            PdfPTable tableTotal = new PdfPTable(7);
            pr = new Phrase("", B10);
            c1 = new PdfPCell(pr);
            c1.setPadding(5);
            tableTotal.addCell(c1);
            pr = new Phrase("", B10);
            c1 = new PdfPCell(pr);
            c1.setPadding(5);
            tableTotal.addCell(c1);

            pr = new Phrase("Total", B10);
            c1 = new PdfPCell(pr);
            c1.setPadding(5);
            c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableTotal.addCell(c1);

            pr = new Phrase(Globals.myNumberFormat2QtyDecimal(total_qty, qty_decimal_check), B10);
            c3 = new PdfPCell(pr);
            c3.setPadding(5);
            c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableTotal.addCell(c3);
            pr = new Phrase("", B10);
            PdfPCell c234 = new PdfPCell(pr);
            c234.setPadding(5);
            c234.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableTotal.addCell(c234);
            pr = new Phrase(Globals.myNumberFormat2Price(total_price, decimal_check), B10);
            c3 = new PdfPCell(pr);
            c3.setPadding(5);
            c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableTotal.addCell(c3);

            pr = new Phrase(Globals.myNumberFormat2Price(total_amount, decimal_check), B10);
            c3 = new PdfPCell(pr);
            c3.setPadding(5);
            c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableTotal.addCell(c3);
            document.open();


//            PdfPTable table_payment = new PdfPTable(7);
//
//            pr = new Phrase("", B10);
//            c1 = new PdfPCell(pr);
//            c1.setPadding(5);
//            c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            c1.setBorder(Rectangle.NO_BORDER);
//            table_payment.addCell(c1);
//
//            pr = new Phrase("", B10);
//            c1 = new PdfPCell(pr);
//            c1.setPadding(5);
//            c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            c1.setBorder(Rectangle.NO_BORDER);
//            table_payment.addCell(c1);
//
//            pr = new Phrase("", B10);
//            c1 = new PdfPCell(pr);
//            c1.setPadding(5);
//            c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            c1.setBorder(Rectangle.NO_BORDER);
//            table_payment.addCell(c1);
//
//            pr = new Phrase("", B10);
//            c1 = new PdfPCell(pr);
//            c1.setPadding(5);
//            c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            c1.setBorder(Rectangle.NO_BORDER);
//            table_payment.addCell(c1);
//
//            pr = new Phrase("", B10);
//            c1 = new PdfPCell(pr);
//            c1.setPadding(5);
//            c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            c1.setBorder(Rectangle.NO_BORDER);
//            table_payment.addCell(c1);
//
//            pr = new Phrase("Payment Name", B10);
//            c1 = new PdfPCell(pr);
//            c1.setPadding(5);
//            c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table_payment.addCell(c1);
//
//            pr = new Phrase("Payment", B10);
//            c3 = new PdfPCell(pr);
//            c3.setPadding(5);
//            c3.setHorizontalAlignment(Element.ALIGN_LEFT);
//            table_payment.addCell(c3);
//
//
//            for (int i = 0; i < list8a.size(); i++) {
//                pr = new Phrase("", N9);
//                PdfPCell c7 = new PdfPCell(pr);
//                c7.setPadding(5);
//                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                c7.setBorder(Rectangle.NO_BORDER);
//                table_payment.addCell(c7);
//
//                pr = new Phrase("", N9);
//                c7 = new PdfPCell(pr);
//                c7.setPadding(5);
//                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                c7.setBorder(Rectangle.NO_BORDER);
//                table_payment.addCell(c7);
//
//                pr = new Phrase("", N9);
//                c7 = new PdfPCell(pr);
//                c7.setPadding(5);
//                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                c7.setBorder(Rectangle.NO_BORDER);
//                table_payment.addCell(c7);
//
//                pr = new Phrase("", N9);
//                c7 = new PdfPCell(pr);
//                c7.setPadding(5);
//                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                c7.setBorder(Rectangle.NO_BORDER);
//                table_payment.addCell(c7);
//
//                pr = new Phrase("", N9);
//                c7 = new PdfPCell(pr);
//                c7.setPadding(5);
//                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                c7.setBorder(Rectangle.NO_BORDER);
//                table_payment.addCell(c7);
//
//                pr = new Phrase(list8a.get(i), N9);
//                c7 = new PdfPCell(pr);
//                c7.setPadding(5);
//                c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                table_payment.addCell(c7);
//
//                pr = new Phrase(list9a.get(i), N9);
//                c7 = new PdfPCell(pr);
//                c7.setPadding(5);
//                c7.setHorizontalAlignment(Element.ALIGN_LEFT);
//                table_payment.addCell(c7);
//
//
//            }
//            table_payment.setSpacingBefore(10.0f);
//            table.setHeaderRows(1);
//            document.open();

//            PdfPTable tablePaymentTotal = new PdfPTable(7);
//
//            pr = new Phrase("", N9);
//            PdfPCell c7 = new PdfPCell(pr);
//            c7.setPadding(5);
//            c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            c7.setBorder(Rectangle.NO_BORDER);
//            tablePaymentTotal.addCell(c7);
//
//            pr = new Phrase("", N9);
//            c7 = new PdfPCell(pr);
//            c7.setPadding(5);
//            c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            c7.setBorder(Rectangle.NO_BORDER);
//            tablePaymentTotal.addCell(c7);
//
//            pr = new Phrase("", N9);
//            c7 = new PdfPCell(pr);
//            c7.setPadding(5);
//            c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            c7.setBorder(Rectangle.NO_BORDER);
//            tablePaymentTotal.addCell(c7);
//
//            pr = new Phrase("", N9);
//            c7 = new PdfPCell(pr);
//            c7.setPadding(5);
//            c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            c7.setBorder(Rectangle.NO_BORDER);
//            tablePaymentTotal.addCell(c7);
//
//            pr = new Phrase("", N9);
//            c7 = new PdfPCell(pr);
//            c7.setPadding(5);
//            c7.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            c7.setBorder(Rectangle.NO_BORDER);
//            tablePaymentTotal.addCell(c7);
//
//            pr = new Phrase("Total", B10);
//            c1 = new PdfPCell(pr);
//            c1.setPadding(5);
//            c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            tablePaymentTotal.addCell(c1);
//
//            pr = new Phrase(Globals.myNumberFormat2Price(total_payment, decimal_check), B10);
//            c1 = new PdfPCell(pr);
//            c1.setPadding(5);
//            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
//            tablePaymentTotal.addCell(c1);
//            document.open();
//            document.add(image);
            document.add(tableh);
            document.add(company_name);
            document.add(gst_no);
            document.add(address);
            document.add(email);
            document.add(mobile);
            document.add(table_ob);

            document.add(table_cash_total);

            document.add(table_total);
            document.add(table);
            document.add(tableTotal);
//            document.add(table_payment);
//            document.add(tablePaymentTotal);

            document.close();
            file.close();
            if (f.exists()) {
                Uri path = Uri.fromFile(f);
                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(path, "application/pdf");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                try {
                    if (s.equals("view")) {
                        startActivity(pdfIntent);
                    }

                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(),
                            "No Application available to view pdf",
                            Toast.LENGTH_LONG).show();
                }
            }

        } catch (Exception e) {
            f.delete();
        }
    }

    @Override
    public void onBackPressed() {
        pDialog = new ProgressDialog(CustomerReturnListActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getString(R.string.Wait_msg));
        pDialog.show();
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    Intent intent = new Intent(CustomerReturnListActivity.this, ReturnOptionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    pDialog.dismiss();
                    finish();
                } finally {
                }
            }
        };
        timerThread.start();
    }
}
