package org.phomellolitepos.CheckBoxClass;

/**
 * Created by LENOVO on 10/17/2017.
 */

public class PaymentInvoiceCheck {
    private String invoice_no;
    private String invoice_date;
    private String amount;
    private boolean selected;

    public String getInvoice_no() {
        return invoice_no;
    }

    public void setInvoice_no(String invoice_no) {
        this.invoice_no = invoice_no;
    }

    public String getInvoice_date() {
        return invoice_date;
    }

    public void setInvoice_date(String invoice_date) {
        this.invoice_date = invoice_date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
