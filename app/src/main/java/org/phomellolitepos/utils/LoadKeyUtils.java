package org.phomellolitepos.utils;

import android.util.Log;

import com.basewin.services.ServiceManager;

/**
 * Created by wangdh on 2016/12/2.
 * name：
 * 描述：
 */

public class LoadKeyUtils {
    private static final String TAG = LoadKeyUtils.class.getName();

    public static boolean updateWorkKey(String data) {
        if (data == null || (data.length() != 48 && data.length() != 80 && data.length() != 112 && data.length() != 120 && data.length() != 168)) {
            Log.e(TAG, "Work key length error！");
            return false;
        }
        Log.i(TAG, "data[" + data.length() + "]=" + data);
        boolean dataPin = false;
        boolean dataMac = false;
        boolean dataTdk = false;
        try {
            Log.d(TAG, "data.substring(0, 32)=" + data.substring(0, 32));
            Log.d(TAG, "data.substring(32, 40)=" + data.substring(32, 40));
            Log.d(TAG, "data.substring(40, 56)=" + data.substring(40, 56));
            Log.d(TAG, "data.substring(72, 80)=" + data.substring(72, 80));
            Log.d(TAG, "data.substring(80, 112)=" + data.substring(80, 112));
            Log.d(TAG, "data.substring(112, 120)=" + data.substring(112, 120));
//            int tmk_index = Integer.parseInt(SPTools.get(SetTmkIndexActivity.KEY_TMK_INDEX, AppConfig.DEFAULT_VALUE_TMK_INDEX));
//            dataPin = mPinPadService.loadPinKeyNew(tmk_index, data.substring(0, 32),
//                    data.substring(32, 40));
//            dataMac = mPinPadService.loadMacKeyNew(tmk_index, data.substring(40, 56),
//                    data.substring(72, 80));
//            dataTdk = mPinPadService.loadTDKeyNew(tmk_index, data.substring(80, 112),
//                    data.substring(112, 120));
            dataPin = ServiceManager.getInstence().getPinpad().loadPinKey(data.substring(0, 32),
                    data.substring(32, 40));
            dataMac = ServiceManager.getInstence().getPinpad().loadMacKey(data.substring(40, 56),
                    data.substring(72, 80));
            dataTdk = ServiceManager.getInstence().getPinpad().loadTDKey(data.substring(80, 112),
                    data.substring(112, 120));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "dataPin=" + dataPin);
        Log.d(TAG, "dataMac=" + dataMac);
        Log.d(TAG, "dataTdk=" + dataTdk);

        if (dataPin && dataMac && dataTdk) {
            return true;
        } else {
            return false;
        }

    }
}
