package org.phomellolitepos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.phomellolitepos.MainActivity;
import org.phomellolitepos.PaymentCollectionActivity;
import org.phomellolitepos.R;
import org.phomellolitepos.ReportViewerActivity;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.Paging;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Pay_Collection;

import java.util.ArrayList;

/**
 * Created by LENOVO on 6/21/2018.
 */

public class PagingAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Paging> data;

    public PagingAdapter(Context context,
                         ArrayList<Paging> list) {
        this.context = context;
        data = list;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        TextView txt_page_no;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.paging_spinner_item,
                viewGroup, false);

        Paging resultp = data.get(i);

        txt_page_no = (TextView) itemView.findViewById(R.id.txt_page_no);
        txt_page_no.setText(resultp.getPage_number() + "");
        return itemView;
    }
}
