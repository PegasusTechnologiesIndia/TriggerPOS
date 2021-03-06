package org.phomellolitepos.pboc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;

import com.basewin.aidl.OnPBOCListener;
import com.basewin.aidl.OnPinInputListener;
import com.basewin.commu.Commu;
import com.basewin.commu.define.CommuListener;
import com.basewin.commu.define.CommuParams;
import com.basewin.commu.define.CommuStatus;
import com.basewin.commu.define.CommuType;
import com.basewin.define.CardType;
import com.basewin.define.InputPBOCOnlineData;
import com.basewin.define.OutputCardInfoData;
import com.basewin.define.OutputMagCardInfo;
import com.basewin.define.OutputPBOCAAData;
import com.basewin.define.OutputQPBOCResult;
import com.basewin.define.PBOCTransactionResult;
import com.basewin.packet8583.exception.Packet8583Exception;
import com.basewin.services.PinpadBinder;
import com.basewin.services.ServiceManager;
import com.basewin.utils.BCDHelper;

import org.phomellolitepos.PaymentActivity;
import org.phomellolitepos.Util.Globals;
import org.phomellolitepos.utils.CallDialog;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * PBOC监听过程(PBOC listener process)
 */
public class onlinePBOCListener implements OnPBOCListener {

    private static final int ONLINE_PROCESS_COMMU = 1;
    private static final int ONLINE_PROCESS_FINISH = 2;
    private final int PIN_DIALOG_SHOW = 1;      //display pinpad(Pinpad弹出)
    private final int PIN_DIALOG_DISMISS = 2;   //close pinpad(Pinpad关闭)
    private final int PIN_SHOW = 3;             //display inputting the pinpad(PIN输入值的显示)
    private final int SETLAYOUT = 4;            //set key layout the pinpad(设置keys布局)
    private final int GETLAYOUT = 5;            //get key layout the pinpad(获取keys布局)
    private PaymentActivity ba;                    //context(上下文)
//    private PinInputDialog pindialog = null;
//    private Iso8583Mgr mIso8583Mgr = null;
    private Commu commu = null;
    private byte[] sendData = null;
    private byte[] keylayout = new byte[96];
    private byte[] receiveData = null;
    private int cardtype = 0;
    private String cardno;
    private String amt;
    CallDialog callDialog;

    //pinpad the callback(pinpad的回调)
    private OnPinInputListener pinpadListener = new OnPinInputListener() {

        @Override
        public void onInput(int len, int key) throws RemoteException {
            //returns the pinpad the length of the input, the key is invalid(返回pinpad输入中的长度，Key无效)
            //ba.LOGD("Pinpad password length in the display:" + len);
            Message message = new Message();
            message.what = PIN_SHOW;
            Bundle bundle = new Bundle();
            bundle.putInt("len", len);
            bundle.putInt("key", key);
            message.setData(bundle);
            pinpad_model.sendMessage(message);
        }

        @Override
        public void onError(int errorCode) throws RemoteException {
            //pinpad result to error(pinpad出错)
            //ba.LOGD("Pinpad error code:" + errorCode);
            pinpad_model.sendEmptyMessage(PIN_DIALOG_DISMISS);
        }

        @Override
        public void onConfirm(byte[] data, boolean isNonePin) {
            //the user to identify the input password,this Data is cryptography encrypted to the password(用户确定了输入的密码,Data为加密了的密码密文)
            if (!isNonePin) {
                //Encrypted transaction(加密交易)
                //ba.LOGD("Pinpad enter password over,encrypt data:" + BCDHelper.hex2DebugHexString(data, data.length));
                pinpad_model.sendEmptyMessage(PIN_DIALOG_DISMISS);
                if (cardtype == CardType.IC_CARD) {
                    try {
                        ServiceManager.getInstence().getPboc().comfirmPinpad(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    online_transaction.sendEmptyMessage(ONLINE_PROCESS_COMMU);
                }
            } else {
                //no secret trading(无密交易)
                //ba.LOGD("Pinpad not encrypt transaction");
                pinpad_model.sendEmptyMessage(PIN_DIALOG_DISMISS);
                if (cardtype == CardType.IC_CARD) {
                    try {
                        ServiceManager.getInstence().getPboc().comfirmPinpad(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    online_transaction.sendEmptyMessage(ONLINE_PROCESS_COMMU);
                }
            }

        }

        @Override
        public void onCancel() throws RemoteException {
            //if you click on the cancel button(点了取消按钮)
            //ba.LOGD("Pinpad User cancel");
            pinpad_model.sendEmptyMessage(PIN_DIALOG_DISMISS);
        }

        @Override
        public void onPinpadShow(byte[] data) throws RemoteException {
            //result Key values,use this on setting the pinpad layout(从底层返回键值，使用此去设置密码键盘)
            //ba.LOGD("Pinpad data is enter password coordinate values");
            Message message = new Message();
            message.what = SETLAYOUT;
            message.obj = data;
            pinpad_model.sendMessage(message);
        }
    };

    //pinpad process control(pinpad流程控制)
    private Handler pinpad_model = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case PIN_DIALOG_SHOW:
                    //display pinpad(显示pinpad)
                    //ba.LOGD("Pinpad show");
                    //ba.dismissDialog();
                    try {
                        //set the pinpad display mode(设置pinpad显示模式)
                        ServiceManager.getInstence().getPinpad().setPinpadMode(PinpadBinder.MODE_RANDOM);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    //Pinpad parameter settings[context,card no,tips,amount,callback](pinpad参数设置[上下文,卡号,提示,金额,回调])
//                    pindialog = new PinInputDialog(ba, cardno, "Please enter the Bank card password", amt, new OnPinDialogListener() {
//
//                        @Override
//                        public void OnPinInput(int result) {
//
//                        }
//
//                        @Override
//                        public void OnCreateOver() {
//                            ba.LOGD("Pinpad View create success");
//                            pinpad_model.sendEmptyMessage(GETLAYOUT);
//                        }
//                    });

                    break;

                case PIN_DIALOG_DISMISS:
                    //close pinpad(关闭pinpad)
//                    ba.LOGD("Pinpad Close");
//                    if (pindialog != null) {
//                        pindialog.dismiss();
//                        pindialog = null;
//                    }
                    break;
                case PIN_SHOW:
                    //according to the length of the input password(显示输入的密码长度)
//                    ba.LOGD("Pinpad display password length");
//                    if (pindialog != null) {
//                        Bundle bundle = msg.getData();
//                        pindialog.setPins(bundle.getInt("len"), bundle.getInt("key"));
//                    }
                    break;
                case SETLAYOUT:
                    //set layout(设置布局)
//                    ba.LOGD("Pinpad start set view" + BCDHelper.bcdToString((byte[]) msg.obj, 0, ((byte[]) msg.obj).length));
//                    pindialog.setKeyShow((byte[]) msg.obj, new OnGetLayoutSucListener() {
//                        @Override
//                        public void onSuc() {
//                            keylayout = pindialog.getKeyLayout();
//                            ba.LOGD("Pinpad start setting view:" + BCDHelper.bcdToString(keylayout, 0, keylayout.length));
//                            try {
//                                ServiceManager.getInstence().getPinpad().setPinpadLayout(keylayout);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });

                    break;
                case GETLAYOUT:
                    //get layout(获取布局)
                    try {
                        ServiceManager.getInstence().getPinpad().setOnPinInputListener(pinpadListener);
                        //set the pinpad information[key index,card no,password length](设置pinpad相关信息)
//                        switch(GlobalData.getInstance().getPinpadVersion())
//                        {
//                            case PinpadInterfaceVersion.PINPAD_INTERFACE_VERSION1:
//                                ServiceManager.getInstence().getPinpad().inputOnlinePin(cardno, new byte[]{0, 6, 12});
//                                break;
//                            case PinpadInterfaceVersion.PINPAD_INTERFACE_VERSION2:
//                                ServiceManager.getInstence().getPinpad().inputOnlinePinNew(GlobalData.getInstance().getTmkId(),cardno, new byte[]{0, 6, 12});
//                                break;
//                            case PinpadInterfaceVersion.PINPAD_INTERFACE_VERSION3:
//                                ServiceManager.getInstence().getPinpad().inputOnlinePinByArea(GlobalData.getInstance().getArea(), GlobalData.getInstance().getTmkId(),cardno, new byte[]{0, 6, 12});
//                                break;
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

    };

    private CommuListener mCommuListener = new CommuListener() {
        @Override
        public void OnStatus(int paramInt, byte[] paramArrayOfByte) {
            switch (paramInt) {
                case CommuStatus.INIT_COMMU:
//                    ba.LOGD("PBOC Communication init:" + CommuStatus.getStatusMsg(CommuStatus.INIT_COMMU));
//                    ba.freshProcessDialog("commu init...");
                    break;
                case CommuStatus.CONNECTING:
//                    ba.LOGD("PBOC Communication connecting:" + CommuStatus.getStatusMsg(CommuStatus.CONNECTING));
//                    ba.freshProcessDialog("commu connecting...");
                    break;
                case CommuStatus.SENDING:
//                    ba.LOGD("PBOC Communication sending:" + CommuStatus.getStatusMsg(CommuStatus.SENDING));
//                    ba.freshProcessDialog("commu send data...");
                    break;
                case CommuStatus.RECVING:
//                    ba.LOGD("PBOC Communication recving:" + CommuStatus.getStatusMsg(CommuStatus.RECVING));
//                    ba.freshProcessDialog("commu recv data...");
                    break;
                case CommuStatus.FINISH:
//                    ba.LOGD("PBOC Communication finish:" + CommuStatus.getStatusMsg(CommuStatus.FINISH));
//                    ba.freshProcessDialog("commu finish...");
                    System.arraycopy(paramArrayOfByte, 0, receiveData, 0, paramArrayOfByte.length);
                    online_transaction.sendEmptyMessage(ONLINE_PROCESS_FINISH);
                    break;
                default:
                    break;
            }

        }

        @Override
        public void OnError(int paramInt, String paramString) {
//            ba.LOGD("PBOC Communication error code:" + paramInt + " error:" + paramString);
//            ba.freshProcessDialog("commu finish...");
            online_transaction.sendEmptyMessage(ONLINE_PROCESS_FINISH);
        }
    };

    //online trading the process(在线支付过程)
    private Handler online_transaction = new Handler() {
        @SuppressLint("ShowToast")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ONLINE_PROCESS_COMMU:
                    //ISO8583 processing the start(ISO流程开始)
                  //  ba.freshProcessDialog("commu with server...");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
//                            mIso8583Mgr = new Iso8583Mgr(ba);
//                            ba.LOGD("PBOC ISO8583 encode");
//                            try {
//                                //packaging(封装)
//                               // sendData = mIso8583Mgr.packData();
//                            } catch (Packet8583Exception | UnsupportedEncodingException e) {
//                                e.printStackTrace();
//                            }
                            commu = Commu.getInstence();

                            //If you do not use in the configuration file configuration, dynamic configuration will be used to code(如果不使用配置文件中的配置，将使用代码动态配置)
                           // ba.LOGD("PBOC Communication no configuration file");
                            commu.setCommuParams(getParams());

                            commu.dataCommu(ba, sendData, mCommuListener);
                        }
                    }).start();
                    break;
                case ONLINE_PROCESS_FINISH:
                    //ISO8583 processing the end(ISO8583流程结束)
                    //ba.LOGD("PBOC Communication over decode data");
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            if (mIso8583Mgr == null) {
//                                //mIso8583Mgr = new Iso8583Mgr(ba);
//                            }
//                            //parsing(解析)
////                            mIso8583Mgr.unpackData(receiveData);
////                            ba.LOGD("PBOC Communication decode:" + mIso8583Mgr.getBitData(3));
////                            ba.dismissDialog();
////                            ba.LOGD("commu finish!");
//                            InputPBOCOnlineData onlineData = new InputPBOCOnlineData();
//                            onlineData.setResponseCode("00");
////                            onlineData.setAuthCode("");
////                            onlineData.setICData("");
//                            try {
//                                ServiceManager.getInstence().getPboc().inputOnlineProcessResult(onlineData.getIntent());
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }.start();
                    break;
                default:
                    break;
            }

        }
    };

    public onlinePBOCListener(PaymentActivity ba, String amt) {
        this.ba = ba;
        this.amt = amt;
    }

    private CommuParams getParams() {
        //Dynamic setting configuration file(动态设置配置文件)
        CommuParams params = new CommuParams();
        params.setIp("140.206.168.98");
        params.setType(CommuType.SOCKET);
        params.setPort(4900);
        params.setTimeout(5);
        return params;
    }

    @Override
    public void onStartPBOC() throws RemoteException {
        //PBOC process the start(PBOC流程开始)
//        ba.LOGD("PBOC Start");
//        ba.freshProcessDialog("start pboc...");
    }

    @Override
    public void onRequestAmount() {
        //if you don't set the amount before,can be in this setting(如果之前没有设置金额，可以再次设置)
       // ba.LOGD("PBOC Setting amount");
//        ba.dismissDialog();
//        new EnterDialog(ba).showEnterDialog("please enter amount!", new OnEnterListener() {
//            @Override
//            public void onEnter(String text) {
//                try {
//                    ServiceManager.getInstence().getPboc().setAmount(Integer.parseInt(StringHelper.changeAmout(text).replace(".","")));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    @Override
    public void onSelectApplication(List<String> applicationList) {
        //selection card application(在此选着卡应用)
//        ba.LOGD("PBOC Select Application");
//        ba.dismissDialog();
//        new EnterDialog(ba).showListChoseDialog("please chose application!", (String[])applicationList.toArray(), new OnChoseListener() {
//            @Override
//            public void Chose(int i) {
//                ba.LOGD("chose :"+i);
//                try {
//                    ServiceManager.getInstence().getPboc().selectApplication(i+1);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    @Override
    public void onFindingCard(int cardType, Intent data) {
        //find and identify the card as well as read relevant data(寻卡和选卡，然后读取相关数据)
//        ba.LOGD("PBOC Finding Choose card");
//        ba.freshProcessDialog("finding card...");
        switch (cardType) {
            case CardType.MAG_CARD:
                cardtype = CardType.MAG_CARD;
                //ba.LOGD("PBOC CardType:Mag card");
                //MAG card data entity class
                OutputMagCardInfo magCardInfo = new OutputMagCardInfo(data);

//                ba.LOGD("PBOC Mag card number:" + magCardInfo.getPAN());
//                ba.LOGD("PBOC Mag card track 2:" + magCardInfo.getTrack2HexString());
//                ba.LOGD("PBOC Mag card track 3:" + magCardInfo.getTrack3HexString());
//                ba.LOGD("PBOC Term of validity:" + magCardInfo.getExpiredDate());
//                ba.LOGD("PBOC Service Code: " + magCardInfo.getServiceCode());
                cardno = magCardInfo.getPAN();
                Globals.Param1 = cardno;
                Globals.Param2 = magCardInfo.getExpiredDate();
//                ba.dismissDialog();
//                pinpad_model.sendEmptyMessage(PIN_DIALOG_SHOW);
                callDialog = new CallDialog(ba,amt);
                callDialog.show();
                break;
            case CardType.IC_CARD:
                cardtype = CardType.IC_CARD;
                //ba.LOGD("PBOC CardType:IC card");
                break;
            case CardType.RF_CARD:
                cardtype = CardType.RF_CARD;
                //ba.LOGD("PBOC CardType:RF card");
                break;
        }
    }


    @Override
    public void onRequestInputPIN(boolean isOnlinePin, int retryTimes) throws RemoteException {
        // Need a password,At this point you need to call password pinpad(底层返回需要设置密码，这个时候需要调用pinpad模块进行密码输入，只有IC PBOC流程)
       // ba.LOGD("PBOC Request input PIN");
        pinpad_model.sendEmptyMessage(PIN_DIALOG_SHOW);
    }

    @Override
    public void onConfirmCardInfo(Intent info) {
        //may need to confirm the IC card information display interface(确认IC卡卡号信息的时候，可能需要进行界面显示，此处略过，最后确认完了调用confirmCardInfo()即可)
//        ba.LOGD("PBOC Confirm Card Info");
//        ba.dismissDialog();
//        OutputCardInfoData out = new OutputCardInfoData(info);
//        ba.LOGD("IC card SN:" + out.getCardSN());
//        ba.LOGD("IC card number:" + out.getPAN());
//        ba.LOGD("IC card expired date:" + out.getExpiredDate());
//        ba.LOGD("IC card service code:" + out.getServiceCode());
//        ba.LOGD("IC card track:" + out.getTrack());
//        cardno = out.getPAN();
//        new EnterDialog(ba).showConfirmDialog("please confirm cardno!", out.getPAN(), new OnConfirmListener() {
//            @Override
//            public void OK() {
//                try {
//                    ServiceManager.getInstence().getPboc().confirmCardInfo();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void Cancel() {
//
//            }
//        });

    }

    @Override
    public void onConfirmCertInfo(String certType, String certInfo) throws RemoteException {
        //confirm the identity(确认身份信息)
      //  ba.LOGD("PBOC Confirm credentials info");
    }

    @Override
    public void onAARequestOnlineProcess(Intent actionAnalysisData) throws RemoteException {
        //online trading(联机交易)
//        ba.LOGD("PBOC the Online trade process");
//
//        OutputPBOCAAData out = new OutputPBOCAAData(actionAnalysisData);
//        ba.LOGD("PBOC 55 field:" + out.get55Field());
//        ba.LOGD("PBOC AA result:" + out.getAAResult());
//        ba.LOGD("PBOC Card seq number:" + out.getCardSeqNum());
//        ba.LOGD("PBOC IC data:" + out.getICData());
//        ba.LOGD("PBOC reversal data:" + out.getReversalData());
//        ba.LOGD("PBOC TC:" + out.getTCData());

        //jump the ISO8583 to encapsulate(跳转到ISO8583封装)
        online_transaction.sendMessage(online_transaction.obtainMessage(ONLINE_PROCESS_COMMU));
    }

    @Override
    public void onTransactionResult(int result, Intent data) throws RemoteException {
        //Transaction result(交易结果)
        //ba.LOGD("PBOC the Transaction result");

        if (result == PBOCTransactionResult.QPBOC_ARQC) {
            //quick pay to process(快速交易流程)
            OutputQPBOCResult rf_data = new OutputQPBOCResult(data);
            String field55String = rf_data.get55Field();
            String pan = rf_data.getPAN();
            cardno = rf_data.getPAN();
            String maskedpan = rf_data.getMaskedPan();
            String trackString = rf_data.getTrack();
           // ba.LOGD("PBOC Trade result track 2:" + trackString);
            byte[] bcdTrack = BCDHelper.StrToBCD(trackString, trackString.length());
            //ba.LOGD("PBOC Trade result track 2 the bcd:" + BCDHelper.hex2DebugHexString(bcdTrack, bcdTrack.length));

            String expiredate = rf_data.getExpiredDate();
            //ba.LOGD("PBOC call PinPad");
            pinpad_model.sendEmptyMessage(PIN_DIALOG_SHOW);
        } else if (result == PBOCTransactionResult.APPROVED) {
            //normal pay to process(普通交易流程)
            try {
               // ba.LOGD("PBOC EC balance：" + ServiceManager.getInstence().getPboc().readEcBalance());

                byte[] data1 = ServiceManager.getInstence().getPboc().getEmvTlvData(0x9F5D);
                if (data1 != null) {
                    //ba.LOGD("data 1:" + BCDHelper.hex2DebugHexString(data1, data1.length));
                }

                byte[] data2 = ServiceManager.getInstence().getPboc().getEmvTlvData(0x9F79);
                if (data2 != null) {
                   // ba.LOGD("data 2:" + BCDHelper.hex2DebugHexString(data2, data2.length));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (result == PBOCTransactionResult.TERMINATED) {
            //transaction the fail as well as stopping the transaction(交易失败并且停止交易)
            //ba.LOGD("PBOC Transaction fail");
        }
    }

    @Override
    public void onReadECBalance(Intent ecBalance) throws RemoteException {
        //online trading the balance,but temporarily didn't use him(在线余额，暂时没有使用)
        //ba.LOGD("PBOC EC balance");

    }

    @Override
    public void onReadCardOfflineRecord(Intent contents) throws RemoteException {
        //offline trading the balance,but temporarily didn't use him(离线余额，暂时没有使用)
        //ba.LOGD("PBOC Transaction record");
    }

    @Override
    public void onError(Intent result) throws RemoteException {
        //PBOC process to error(流程出错)
       // ba.LOGD("PBOC Error");
//        ba.dismissDialog();
    }
}
