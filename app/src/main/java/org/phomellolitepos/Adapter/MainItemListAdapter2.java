package org.phomellolitepos.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.codec.Base64;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.phomellolitepos.ChangePriceActivity;
import org.phomellolitepos.ContactActivity;
import org.phomellolitepos.ContactListActivity;
import org.phomellolitepos.Fragment.ItemFragment2;
import org.phomellolitepos.ItemActivity;
import org.phomellolitepos.ItemListActivity;
import org.phomellolitepos.Main2Activity;
import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.Util.RecyclerTouchListener;
import org.phomellolitepos.database.Contact;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group_Tax;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Lite_POS_Registration;
import org.phomellolitepos.database.Order_Item_Tax;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.ShoppingCart;
import org.phomellolitepos.database.Sys_Tax_Type;
import org.phomellolitepos.database.Tax_Detail;
import org.phomellolitepos.database.Tax_Master;
import org.phomellolitepos.utils.ImageCache;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import sunmi.bean.SecondScreenData;
import sunmi.ds.DSKernel;
import sunmi.ds.callback.IConnectionCallback;
import sunmi.ds.callback.IReceiveCallback;
import sunmi.ds.callback.ISendCallback;
import sunmi.ds.callback.ISendFilesCallback;
import sunmi.ds.data.DSData;
import sunmi.ds.data.DSFile;
import sunmi.ds.data.DSFiles;
import sunmi.ds.data.Data;
import sunmi.ds.data.DataPacket;
import sunmi.ds.data.UPacketFactory;
import woyou.aidlservice.jiuiv5.IWoyouService;

import static com.squareup.picasso.Picasso.Priority.HIGH;

/**
 * Created by LENOVO on 11/8/2017.
 */

public class MainItemListAdapter2 extends RecyclerView.Adapter<MainItemListAdapter2.MyViewHolder> {
    Context context;
    LayoutInflater inflater;
    String result1;
    ArrayList<Item> data;
    Item_Location item_location;
    String decimal_check, qty_decimal_check;
    Settings settings;
    SQLiteDatabase database;
    Database db;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_item_name, txt_price;
        ImageView item_image;

        public MyViewHolder(View view) {
            super(view);
            item_image = (ImageView) itemView.findViewById(R.id.item_image);
            item_image.setImageDrawable(null);
            txt_item_name = (TextView) itemView.findViewById(R.id.txt_item_name);
            txt_price = (TextView) itemView.findViewById(R.id.txt_price);
        }
    }

    public MainItemListAdapter2(Context context,ArrayList<Item> list) {
        this.context = context;
        data = list;
    }


    @Override
    public MainItemListAdapter2.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_view_list_item, parent, false);
        return new MainItemListAdapter2.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MainItemListAdapter2.MyViewHolder holder, int position) {
        Item resultp = data.get(position);

        db = new Database(context);
        database = db.getWritableDatabase();
//        if (database.isOpen()){
//
//        }else {}
        settings = Settings.getSettings(context, database, "");
        try {
            decimal_check = Globals.objLPD.getDecimal_Place();
            qty_decimal_check = settings.get_Qty_Decimal();
        } catch (Exception ex) {
            decimal_check = "1";
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
        holder.item_image.setLayoutParams(layoutParams);
        try {
            if (resultp.get_item_image().equals("") || resultp.get_item_image().toString().equals("null") || resultp.get_item_image().toString().equals("0")) {
                InputStream imageIS = context.getResources().openRawResource(R.raw.recipemaker);
                Bitmap bitmap = BitmapFactory.decodeStream(imageIS);
                holder.item_image.setImageBitmap(bitmap);
            } else {
                try {
                    holder.item_image.setImageBitmap(
                            ImageCache.decodeSampledBitmapFromResource(resultp.get_item_image(), 100, 100));
                }catch (Exception ex){
                    String ab = ex.getMessage();
                    ab=ab;
                }
            }
        } catch (Exception e) {
            InputStream imageIS = context.getResources().openRawResource(R.raw.recipemaker);
            Bitmap bitmap = BitmapFactory.decodeStream(imageIS);
            holder.item_image.setImageBitmap(bitmap);
        }

        String sale_price = "0";
        try {
            item_location = Item_Location.getItem_Location(context, " WHERE item_code = '" + resultp.get_item_code() + "'", database);
            sale_price = Globals.myNumberFormat2Price(Double.parseDouble(item_location.get_selling_price()), decimal_check);

        } catch (Exception ex) {
            sale_price = Globals.myNumberFormat2Price(Double.parseDouble(sale_price), decimal_check);
        }
        holder.txt_item_name.setText(resultp.get_item_name());
        holder.txt_price.setText(sale_price);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
