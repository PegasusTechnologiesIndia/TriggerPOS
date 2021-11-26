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
import androidx.recyclerview.widget.RecyclerView;
import android.util.Base64;
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

import org.json.JSONObject;
import org.phomellolitepos.R;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Item;
import org.phomellolitepos.database.Item_Group;
import org.phomellolitepos.database.Item_Location;
import org.phomellolitepos.database.Settings;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by LENOVO on 11/8/2017.
 */

public class MainItemListAdapter2 extends RecyclerView.Adapter<MainItemListAdapter2.MyViewHolder> {
    Context context;
    LayoutInflater inflater;
    String result1;
    String lastext;
    ArrayList<Item> data;
    Item_Location item_location;
    String decimal_check, qty_decimal_check;
    Settings settings;
    SQLiteDatabase database;
    Database db;
    ArrayList<String>nameList;

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


                /*try {
                    byte[] decodedString = Base64.decode(resultp.getItem_image(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    // byte[] imgbyte =resultp.get_ItemLogo().getBytes();
                    if (decodedByte != null) {
                        Glide.with(context)
                                .asBitmap()
                                .load(decodedByte)
                                .into(holder.item_image);
                        // Bitmap bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                       *//* holder.item_image.setImageBitmap(decodedByte);
                        holder.item_image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);*//*
                    } else {
                        holder.item_image.setImageResource(R.drawable.recipemaker);
                        holder.item_image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

*/
            File imgFile = null;
/*            if(Globals.arrayListGetFile_Image.size()>0) {
    for(int i=0; i<Globals.arrayListGetFile_Image.size();i++) {
        String folder = Globals.folder + "ItemImages/" + Globals.arrayListGetFile_Image.get(i);
        String lastext = folder.substring(folder.lastIndexOf("."));
   
    imgFile = new  File(Globals.folder + "ItemImages/" + Globals.arrayListGetFile_Image.get(i));
       File files[]= imgFile.listFiles();

        if(imgFile.exists()) {

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            if(myBitmap!=null) {

                holder.item_image.setImageBitmap(myBitmap);
            }
            *//*else{
                InputStream imageIS = context.getResources().openRawResource(R.raw.recipemaker);
                Bitmap bitmap = BitmapFactory.decodeStream(imageIS);
                holder.item_image.setImageBitmap(bitmap);
            }*//*

        }
        else{
            InputStream imageIS = context.getResources().openRawResource(R.raw.recipemaker);
            Bitmap bitmap = BitmapFactory.decodeStream(imageIS);
            holder.item_image.setImageBitmap(bitmap);
        }

    }
}
else {*/
//String imgpath=Globals.folder+"ItemImages/"+ resultp.get_item_code();
         //   String imgpath1=Globals.folder+"ItemImages/" + resultp.get_item_code() + ".jpeg";
             //lastext = imgpath.substring(imgpath.lastIndexOf("/")+1);
/*if(lastext.equals(".jpg")) {*/



            nameList = new ArrayList<String>();
            File yourDir = new File(Globals.folder, "ItemImages/");
         /*   for (File f : yourDir.listFiles())
            {

                if (f.isFile())
                {
                    nameList.add(f.getName());

                }

            }*/


            File[] listFile = yourDir.listFiles();
            if (listFile != null)
            {
                for (int i = 0; i < listFile.length; i++)
                nameList.add(listFile[i].getName());

                for (int i = 0; i < listFile.length; i++) {

                    String fileext = listFile[i].getName().substring(listFile[i].getName().lastIndexOf(".") + 1);
                    // nameList.add(listFile[i].getAbsolutePath());

                    String namefile= listFile[i].getName();
                    int iend = namefile.indexOf("."); //this finds the first occurrence of "."
                    String subString = null;



                    if (iend != -1)
                    {
                        subString= namefile.substring(0 , iend); //this will give abc
                        Arrays.asList(nameList).contains(resultp.get_item_code()+fileext);
                    }
                    Boolean r = null;
                    for (String element : nameList)
                        if (element.equals(resultp.get_item_code()+"."+fileext)) {
                            r = true;
                            break;
                        }
                    // subString.equals(resultp.get_item_code());
                    if (true)
                    {
                        imgFile = new File(Globals.folder, "ItemImages/" + resultp.get_item_code()+"."+fileext);

                        if (imgFile.exists())
                        {
                             Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                             if (myBitmap != null)
                             {
                               holder.item_image.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
                             }
                             else{
//                                 InputStream imageIS = context.getResources().openRawResource(R.raw.recipemaker);
//                                 Bitmap bitmap = BitmapFactory.decodeStream(imageIS);
//                                 holder.item_image.setImageBitmap(bitmap);
//                                 Bitmap bitmap=BitmapFactory.decodeResource(context.getResources(),R.raw.recipemaker);
//                                 holder.item_image.setImageBitmap(bitmap);
                                 }

                        } else {
                                // InputStream imageIS = context.getResources().openRawResource(R.raw.recipemaker);
                                 //Bitmap bitmap = BitmapFactory.decodeStream(imageIS);
//                                Bitmap bitmap=BitmapFactory.decodeResource(context.getResources(),R.raw.recipemaker);
//                                holder.item_image.setImageBitmap(bitmap);
                         //   image.setImageBitmap(bitmap);

                               }
                    }else{
//                        Item item1 = Item.getItem(context, "WHERE item_code = '" + resultp.get_item_code() + "'", database, db);
//                        Item item2 = Item.getItem(context, "WHERE item_code = '" + subString + "'", database, db);
//
//                        Item_Group item_group = Item_Group.getItem_Group(context, database, db, "WHERE item_group_code='" + item1.get_item_group_code() + "'AND is_active=1 COLLATE NOCASE"  );
//                        Item_Group image_group = Item_Group.getItem_Group(context, database, db, "WHERE item_group_code='" + item2.get_item_group_code() + "'AND is_active=1 COLLATE NOCASE"  );


//                        if(item_group.get_item_group_name().equals(image_group.get_item_group_name()))
//                        {
                        Bitmap bitmap=BitmapFactory.decodeResource(context.getResources(),R.raw.recipemaker);
                        holder.item_image.setImageBitmap(bitmap);
              //          }
                    }
                }
            }else
                {

                        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.raw.recipemaker);
                        holder.item_image.setImageBitmap(bitmap);

                }

/*if(nameList.size()>0) {
    for (int i = 0; i < nameList.size(); i++) {
        imgFile = new File(Globals.folder, "ItemImages/" + nameList.get(i));
        if (imgFile.exists()) {

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            if (myBitmap != null) {

                holder.item_image.setImageBitmap(myBitmap);
            }


        } else {
            InputStream imageIS = context.getResources().openRawResource(R.raw.recipemaker);
            Bitmap bitmap = BitmapFactory.decodeStream(imageIS);
            holder.item_image.setImageBitmap(bitmap);
        }
        // Toast.makeText(context, f.getName(), Toast.LENGTH_SHORT).show();
        //System.out.println("File name" + f.getName());
    }
}*/
           /* }
            if(lastext.equals(".png")) {
                imgFile = new File(Globals.folder, "ItemImages/" + resultp.get_item_code() + ".png");
            }if(lastext.equals(".jpeg")) {
                imgFile = new File(Globals.folder, "ItemImages/" + resultp.get_item_code() + ".jpeg");
            }*/

//}



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
