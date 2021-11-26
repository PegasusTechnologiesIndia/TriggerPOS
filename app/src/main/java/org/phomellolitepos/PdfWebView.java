package org.phomellolitepos;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintJobId;
import android.print.PrintManager;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import org.phomellolitepos.Adapter.PdfDocumentAdapter;
import org.phomellolitepos.Util.Globals;

public class PdfWebView extends AppCompatActivity {
Button btn_print;
    String contactcode,Fromgetevent;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_web_view);
        btn_print = (Button) findViewById(R.id.btn_print);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Intent i = getIntent();
        contactcode = i.getStringExtra("contact_code");
        Fromgetevent = i.getStringExtra("from");

btn_print.setOnClickListener(new View.OnClickListener() {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        Globals.AppLogWrite("Accounts Printer type"+Globals.PrinterType);

        String path;
        if(Globals.PrinterType.equals("11")){
           path= Globals.folder + Globals.pdffolder
                    + "/" + contactcode+"80mm" + ".pdf";
        }
        else {
           path = Globals.folder + Globals.pdffolder
                    + "/" + contactcode + ".pdf";
        }
        Globals.AppLogWrite("Accounts path"+path);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            PrintManager printManager = (PrintManager) PdfWebView.this.getSystemService(PRINT_SERVICE);
            try {
                PrintDocumentAdapter printAdapter = new PdfDocumentAdapter(PdfWebView.this, path);
                String jobName = getApplicationContext().getString(R.string.app_name) + " Document";

                PrintJob printJob = printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
                PrintJobId printJobId = printJob.getId();
if(printJobId.equals(true)){
    finish();
}
            } catch (Exception e) {
                Log.e("", e.getMessage());
            }
        }
        //finish();
    }
});


    }

    @Override
    public void onBackPressed() {
        Globals.strContact_Code = "";
        Globals.data.clear();
//        Toast.makeText(getApplicationContext(), getString(R.string.postsuccful), Toast.LENGTH_SHORT).show();
        finish();
//        if(Fromgetevent.equals("CustomerReturn")){
//            Intent intent1 = new Intent(PdfWebView.this, CustomerReturnListActivity.class);
//            startActivity(intent1);
//            finish();
//        }
//        else if(Fromgetevent.equals("InvoiceReturn")) {
//            Intent intent1 = new Intent(PdfWebView.this, InvReturnListActivity.class);
//            startActivity(intent1);
//            finish();
//        }
//        else if(Fromgetevent.equals("Accounts")) {
//            if(Globals.objLPR.getIndustry_Type().equals("3")){
//                Intent intent1 = new Intent(PdfWebView.this, PaymentCollection_MainScreen.class);
//                startActivity(intent1);
//                finish();
//            }
//            else {
//                Intent intent1 = new Intent(PdfWebView.this, AccountsListActivity.class);
//                startActivity(intent1);
//                finish();
//            }
//        }
        super.onBackPressed();

    }


    /*  WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setBuiltInZoomControls(true);
        webview.setWebChromeClient(new WebChromeClient());
        final File file = new File(Globals.folder + Globals.pdffolder
                + "/" + ordercode + ".pdf");

        String doc="<iframe src='"+file+"' width='100%' height='100%' style='border: none;'></iframe>";
       // webview.loadUrl("https://docs.google.com/gview?embedded=true&url=" + file);
        webview.loadData(doc, "text/html", "UTF-8");
        //webview.loadData(file, "application/pdf", "UTF-8");

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createWebPrintJob(WebView webView) {

        //create object of print manager in your device
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

        //create object of print adapter
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();

        //provide name to your newly generated pdf file
        String jobName = getString(R.string.app_name) + " Print Test";

        //open print dialog
        printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
    }

    //perform click pdf creation operation on click of print button click
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void printPDF(View view) {
        createWebPrintJob(webview);
    }*/

}
