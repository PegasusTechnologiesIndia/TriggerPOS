package sunmi.dsc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

import sunmi.ds.DSKernel;
import sunmi.ds.callback.IReceiveCallback;
import sunmi.ds.callback.ISendCallback;
import sunmi.ds.data.DSData;
import sunmi.ds.data.DSFile;
import sunmi.ds.data.DSFiles;
import sunmi.ds.data.Data;
import sunmi.ds.data.DataPacket;
import sunmi.ds.data.UPacketFactory;

/**
 * 主屏关机时副屏同时关机
 * Created by bps on 2016/9/12.
 */
public class ShutDownBroadcastRecevier extends BroadcastReceiver {



    DSKernel mDSKernel;
    DataPacket dsPacket;
    DataPacket rebootDsPacket;
    Gson gson = new Gson();
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "action_shutdown_client", Toast.LENGTH_SHORT).show();
        boolean reboot = intent.getBooleanExtra("reboot", true);
        mDSKernel = DSKernel.newInstance();
        mDSKernel.addReceiveCallback(mReceiveCallback);
        dsPacket = UPacketFactory.buildShutDown(
                DSKernel.getDSDPackageName(), callback);
        rebootDsPacket=  UPacketFactory.buildReboot(DSKernel.getDSDPackageName(), callback);
            if (reboot) {
                mDSKernel.sendData(rebootDsPacket);
            } else {
                mDSKernel.sendData(dsPacket);
            }

    }
    private ISendCallback callback = new ISendCallback() {

        @Override
        public void onSendFail(int arg0, String arg1) {
        }

        @Override
        public void onSendProcess(long arg0, long arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onSendSuccess(long arg0) {
        }

    };

    /**
     * 接收数据的回调
     */
    private IReceiveCallback mReceiveCallback = new IReceiveCallback() {

        @Override
        public void onReceiveFile(DSFile file) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onReceiveFiles(DSFiles dsFiles) {

        }

        @Override
        public void onReceiveData(DSData data) {
            long taskId = dsPacket.getData().taskId;
            Data data4Json = gson.fromJson(data.data, Data.class);
            if (taskId == data.taskId) {
            }
        }

        @Override
        public void onReceiveCMD(DSData data) {
            // TODO Auto-generated method stub
        }
    };

}
