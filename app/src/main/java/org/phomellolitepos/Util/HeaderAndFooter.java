package org.phomellolitepos.Util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;


public class HeaderAndFooter extends PdfPageEventHelper {

    private String name = "";
    protected Phrase footer;
    protected Phrase header;
    private Font headerFont = new Font();
    private Font N9 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL, BaseColor.BLACK);

    public HeaderAndFooter(String name) {
        this.name = name;
        header = new Phrase("***** Header *****");
        footer = new Phrase("**** Footer ****");
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {

        PdfContentByte cb = writer.getDirectContent();

        String headerContent = name;
        //header
        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(
                        headerContent, headerFont), document.leftMargin() - 1,
                document.top(), 0);
        //footer
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase(
                        Globals.myNumberFormat().format(Double.valueOf(writer.getPageNumber())), N9),
                document.right() - 52, document.bottom() - 20, 0);
        //footer
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase(DateUtill.DateTimeAmPPM(), N9),
                (document.left() + document.right()) / 2, document.bottom() - 20, 0);
        //footer
        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("Printed By:" + "user name", N9),
                document.left() + 52, document.bottom() - 20, 0);
    }
}
