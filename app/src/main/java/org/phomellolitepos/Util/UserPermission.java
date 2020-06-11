package org.phomellolitepos.Util;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import org.phomellolitepos.ActivateActivity;
import org.phomellolitepos.Main2Activity;
import org.phomellolitepos.MainActivity;
import org.phomellolitepos.database.Database;
import org.phomellolitepos.database.Settings;
import org.phomellolitepos.database.User;

import java.util.ArrayList;

/**
 * Created by LENOVO on 2/3/2018.
 */

public class UserPermission {
    Database db;
    SQLiteDatabase database;
    User user;
    Boolean result;
    Settings settings;

    public Boolean Permission(Context context, String strTitle, Class<?> cls) {
        db = new Database(context);
        database = db.getWritableDatabase();
        settings = Settings.getSettings(context, database, "");
        try {
            ArrayList<String> arrayList;
            user = User.getUser(context, "Where user_code = '" + Globals.UserCode + "' and is_active='1'", database);
//        user.get_app_user_permission()
            if (user.get_app_user_permission().equals("")) {
//                Intent item_intent = new Intent(context, cls);
//                context.startActivity(item_intent);
            } else {
                arrayList = new ArrayList<>();
                String  strAppPermission  = user.get_app_user_permission();
                strAppPermission = strAppPermission.replace("[","");
                strAppPermission = strAppPermission.replace("]","");
                strAppPermission = strAppPermission.replace("\"","");


                String[] str = strAppPermission.split(",");
                for (int i = 0; i < str.length; i++) {
                    arrayList.add(str[i]);
                }
                result = arrayList.contains(strTitle);
                if (result == false) {
                    Toast.makeText(context, "This user don't have permission to access this form", Toast.LENGTH_LONG).show();
                    if (settings.get_Home_Layout().equals("0")) {
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, Main2Activity.class);
                        context.startActivity(intent);
                    }

                } else {
                    Intent intent = new Intent(context, cls);
                    context.startActivity(intent);
                }
            }
        } catch (Exception ex) {
            Intent item_intent = new Intent(context, cls);
            context.startActivity(item_intent);
        }
        return result;
    }
}
