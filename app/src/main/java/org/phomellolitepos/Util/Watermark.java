package org.phomellolitepos.Util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class Watermark extends PdfPageEventHelper {

    protected Phrase watermark = new Phrase(Globals.Watermark, new Font(FontFamily.HELVETICA, 30,
            Font.NORMAL, new BaseColor(0x90FF0000)));

    protected Phrase watermark1 = new Phrase("Demo Version", new Font(FontFamily.HELVETICA, 30, Font.NORMAL,
            new BaseColor(0x90FF0000)));

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte canvas = writer.getDirectContent();

//        if (JsonHandle2forLIC.IsDemo == 0) {
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 298, 421, 45);
//        }
//        if (JsonHandle2forLIC.IsDemo == 1) {
//            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark1, 298, 421, 45);
//        }
    }
}
