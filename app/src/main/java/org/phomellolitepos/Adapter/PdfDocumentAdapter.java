package org.phomellolitepos.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import androidx.annotation.RequiresApi;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


@RequiresApi(api = Build.VERSION_CODES.KITKAT)

public class PdfDocumentAdapter extends PrintDocumentAdapter {
    Context context;
    private int pageHeight;
    private int pageWidth;

    public int totalpages = 2;
    PdfDocument myPdfDocument;
    String pathName = "";

    public PdfDocumentAdapter(Context ctxt, String pathName) {
        context = ctxt;
        this.pathName = pathName;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onFinish() {
        super.onFinish();
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes,
                         PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal,
                         LayoutResultCallback callback,
                         Bundle metadata) {

        myPdfDocument = new PrintedPdfDocument(context, newAttributes);
   /*     PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create()
        PdfDocument.Page page = myPdfDocument.startPage(pageInfo);*/

   /*     PrintAttributes.MediaSize customSize = new
                PrintAttributes.MediaSize("COIL", "COIL", 5800,40000);
        customSize.asPortrait();*/

        pageHeight =
                newAttributes.getMediaSize().getHeightMils() / 1000 * 72;
        pageWidth =
                newAttributes.getMediaSize().getWidthMils() / 1000 * 72;

        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        if (totalpages > 0) {
            PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                    .Builder(pathName)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(totalpages);

            PrintDocumentInfo info = builder.build();
            callback.onLayoutFinished(info, true);
        } else {
            callback.onLayoutFailed("Page count is zero.");
        }
    }

    @Override
    public void onWrite(final PageRange[] pageRanges,
                        final ParcelFileDescriptor destination,
                        final CancellationSignal cancellationSignal,
                        final WriteResultCallback callback) {
       PdfDocument.Page page;
        for (int i = 0; i < totalpages; i++) {
            if (pageInRange(pageRanges, i)) {
                PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,
                        pageHeight, i).create();

                 page =
                        myPdfDocument.startPage(newPage);

                if (cancellationSignal.isCanceled()) {
                    callback.onWriteCancelled();
                    myPdfDocument.close();
                    myPdfDocument = null;
                    return;
                }
                //drawPage(page, i);
                InputStream in = null;
                OutputStream out = null;
                try {
                    File file = new File(pathName);
                    in = new FileInputStream(file);
                    out = new FileOutputStream(destination.getFileDescriptor());

                    byte[] buf = new byte[16384];
                    int size;

                    while ((size = in.read(buf)) >= 0
                            && !cancellationSignal.isCanceled()) {
                        out.write(buf, 0, size);
                    }

                    if (cancellationSignal.isCanceled()) {
                        callback.onWriteCancelled();
                    } else {
                        callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
                    }
                } catch (Exception e) {
                    callback.onWriteFailed(e.getMessage());
                   Log.e("",e.getMessage());
                } finally {
                    try {
                        if(in!=null) {
                            in.close();
                        }
                        if(out!=null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        Log.e("",e.getMessage());
                    }
                }

            myPdfDocument.finishPage(page);
            }
        }


        try {
            myPdfDocument.writeTo(new FileOutputStream(
                    destination.getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        } finally {
            myPdfDocument.close();
            myPdfDocument = null;
        }

        callback.onWriteFinished(pageRanges);
    }


    private boolean pageInRange(PageRange[] pageRanges, int page)
    {
        for (int i = 0; i<pageRanges.length; i++)
        {
            if ((page >= pageRanges[i].getStart()) &&
                    (page <= pageRanges[i].getEnd()))
                return true;
        }
        return false;
    }

    private void drawPage(PdfDocument.Page page,
                          int pagenumber) {
        Canvas canvas = page.getCanvas();

        pagenumber++; // Make sure page numbers start at 1

        int titleBaseLine = 72;
        int leftMargin = 54;

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        canvas.drawText(
                "Test Print Document Page " + pagenumber,
                leftMargin,
                titleBaseLine,
                paint);

        paint.setTextSize(14);
        canvas.drawText("This is some test content to verify that custom document printing works", leftMargin, titleBaseLine + 35, paint);

        if (pagenumber % 2 == 0)
            paint.setColor(Color.RED);
        else
            paint.setColor(Color.GREEN);

        PdfDocument.PageInfo pageInfo = page.getInfo();


        canvas.drawCircle(pageInfo.getPageWidth()/2,
                pageInfo.getPageHeight()/2,
                150,
                paint);
    }

}
