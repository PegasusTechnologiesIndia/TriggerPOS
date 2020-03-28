package org.phomellolitepos.printer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

public class WifiPrintDriver {

    // private static String encode = "ISO-8859-6";
    // set same encoding on printer also
    private static String encode = "CP864";
    public static Socket mysocket = null;
    public static OutputStream mWifiOutputStream = null;
    private static int mIndex = 0;
    public static final int Code128_B = 732;
    private static byte[] mCmdBuffer = new byte[1048576];

    public static boolean WIFISocket(String ip, int port) {
        boolean error = false;
        if (mysocket != null) {
            try {
                mysocket.close();
            } catch (IOException e) {

            }
            mysocket = null;
        }
        try {
            mysocket = new Socket(ip, port);
            if (mysocket != null) {
                mWifiOutputStream = mysocket.getOutputStream();
            } else {
                mWifiOutputStream = null;
                error = true;
            }
        } catch (UnknownHostException e) {

            return false;
        } catch (IOException e) {

            return false;
        }
        if (error) {
            Close();
            return false;
        }
        return true;
    }

    public static boolean Close() {
        try {
            if (mWifiOutputStream != null) {
                mWifiOutputStream.close();
                mWifiOutputStream = null;
            }
            if (mysocket != null) {
                mysocket.close();
                mysocket = null;
            }
        } catch (IOException e) {

        }
        return true;
    }

    public static boolean IsNoConnection() {
        if (mWifiOutputStream == null) {
            return true;
        }
        return false;
    }

    public static boolean InitPrinter() {
        try {
            byte[] combyte = {27, 64};
            if (mWifiOutputStream == null) {
                return false;
            }
            mWifiOutputStream.write(combyte);
        } catch (IOException e) {

        }
        return true;
    }

    public static void ImportData(byte[] data, int dataLen) {
        int DataLength = dataLen;
        for (int i = 0; i < DataLength; i++) {
            mCmdBuffer[(mIndex++)] = data[i];
        }
    }

    public static void ImportData(String dataString)

    {
        byte[] data = null;
        // byte[] data1 = null;
        try {
            data = dataString.getBytes(encode);

        } catch (UnsupportedEncodingException e) {

        }
        int DataLength = data.length;
        for (int i = 0; i < DataLength; i++) {

            mCmdBuffer[(mIndex++)] = data[i];
        }
    }

    public static long[] convertExtendedAscii(String input) {
        int length = input.length();
        long[] retVal = new long[length];

        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);

            if (c < 127) {
                retVal[i] = (long) c;
            } else {
                retVal[i] = (long) (c - 256);
            }
        }

        return retVal;
    }

    public static void ImportData(String dataString, boolean bGBK) {
        byte[] data = null;
        if (bGBK) {
            try {
                data = dataString.getBytes(encode);
            } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
            }
        } else {
            data = dataString.getBytes();
        }
        int DataLength = data.length;
        for (int i = 0; i < DataLength; i++) {
            mCmdBuffer[(mIndex++)] = data[i];
        }
    }

    @SuppressWarnings("unused")
    private static byte[] ArabicReverse(byte[] arstr) {
        int r = arstr.length - 1;
        byte[] tempstr = new byte[arstr.length + 1];
        int i = 0;
        int k = 0;
        int m = 0;
        m = 0;
        k = (arstr.length - 1);

        for (i = 0; i <= arstr.length - 1; i++) {
            if (!(arstr[(int) (r - i)] >= 48 & arstr[(int) (r - i)] <= 57)
                    & !(arstr[(int) (r - i)] >= 65 & arstr[(int) (r - i)] <= 90)
                    & !(arstr[(int) (r - i)] >= 97 & arstr[(int) (r - i)] <= 122)) {
                tempstr[(int) i] = arstr[(int) (r - i)];

            }
        }

        int gV = arstr.length - 2;
        for (i = 1; i <= tempstr.length - 2; i++) {
            if ((tempstr[(int) i] == 0)) {
                int count = 0;

                for (k = i; k <= tempstr.length - 2; k++) {
                    if ((tempstr[(int) k] == 0)) {
                        count = count + 1;
                    } else {
                        break; // might not be correct. Was : Exit For
                    }
                }

                while ((m < count)) {
                    tempstr[(int) (i + m)] = arstr[(int) ((gV - count) + (m + 1))];
                    m = m + 1;
                }
                m = 0;
                gV = gV - count;
                if ((count > 1)) {
                    i = i + count - 1;
                }

            } else {
                gV = gV - 1;
            }

        }

        arstr = tempstr;

        return arstr;
    }

    public static void ClearData() {
        mIndex = 0;
    }

    public static void WakeUpPritner() {
        byte[] b = new byte[3];
        try {
            mWifiOutputStream.write(b);
            Thread.sleep(100L);
        } catch (Exception e) {

        }
    }

    public static void Begin() {
        WakeUpPritner();
        InitPrinter();

        ClearData();
    }

    public static boolean excute() {
        if (mIndex > 0) {
            try {
                mWifiOutputStream.write(mCmdBuffer, 0, mIndex);
                mWifiOutputStream.flush();
                mIndex = 0;
            } catch (IOException e) {

                return false;
            }
            return true;
        }
        return false;
    }

    public static void LF() {
        mCmdBuffer[(mIndex++)] = 10;
    }

    public static void SetZoom(byte param) {
        mCmdBuffer[(mIndex++)] = 29;
        mCmdBuffer[(mIndex++)] = 33;
        mCmdBuffer[(mIndex++)] = param;
    }

    public static void SetCharacterFont(byte param) {
        mCmdBuffer[(mIndex++)] = 27;
        mCmdBuffer[(mIndex++)] = 77;
        mCmdBuffer[(mIndex++)] = param;
    }

    public static void SetUnderline(byte param) {
        mCmdBuffer[(mIndex++)] = 27;
        mCmdBuffer[(mIndex++)] = 45;
        mCmdBuffer[(mIndex++)] = param;
    }

    public static void AddBold(byte param) {
        mCmdBuffer[(mIndex++)] = 27;
        mCmdBuffer[(mIndex++)] = 69;
        mCmdBuffer[(mIndex++)] = param;
    }

    public static void AddInverse(byte param) {
        mCmdBuffer[(mIndex++)] = 29;
        mCmdBuffer[(mIndex++)] = 66;
        mCmdBuffer[(mIndex++)] = param;
    }

    public static void SetLineSpace(byte param) {
        mCmdBuffer[(mIndex++)] = 27;
        mCmdBuffer[(mIndex++)] = 51;
        mCmdBuffer[(mIndex++)] = 3;
    }

    public static void AddAlignMode(byte param) {
        mCmdBuffer[(mIndex++)] = 27;
        mCmdBuffer[(mIndex++)] = 97;
        mCmdBuffer[(mIndex++)] = param;
    }

    public static void AddCodePrint(int CodeType, String data) {
        switch (CodeType) {
            case 732:
                Code128_B(data);
                break;
        }
    }

    public static void Code128_B(String data) {
        int m = 73;
        int num = data.length();
        int transNum = 0;
        mCmdBuffer[(mIndex++)] = 29;
        mCmdBuffer[(mIndex++)] = 107;
        mCmdBuffer[(mIndex++)] = ((byte) m);
        int Code128C = mIndex;
        mIndex += 1;
        mCmdBuffer[(mIndex++)] = 123;
        mCmdBuffer[(mIndex++)] = 66;
        for (int i = 0; i < num; i++) {
            if ((data.charAt(i) > '') || (data.charAt(i) < ' ')) {
                return;
            }
        }
        if (num > 30) {
            return;
        }
        for (int i = 0; i < num; i++) {
            mCmdBuffer[(mIndex++)] = ((byte) data.charAt(i));
            if (data.charAt(i) == '{') {
                mCmdBuffer[(mIndex++)] = ((byte) data.charAt(i));
                transNum++;
            }
        }
        int checkcodeID = 104;
        int n = 1;
        for (int i = 0; i < num; i++) {
            checkcodeID += n++ * (data.charAt(i) - ' ');
        }
        checkcodeID %= 103;
        if ((checkcodeID >= 0) && (checkcodeID <= 95)) {
            mCmdBuffer[(mIndex++)] = ((byte) (checkcodeID + 32));
            mCmdBuffer[Code128C] = ((byte) (num + 3 + transNum));
        } else if (checkcodeID == 96) {
            mCmdBuffer[(mIndex++)] = 123;
            mCmdBuffer[(mIndex++)] = 51;
            mCmdBuffer[Code128C] = ((byte) (num + 4 + transNum));
        } else if (checkcodeID == 97) {
            mCmdBuffer[(mIndex++)] = 123;
            mCmdBuffer[(mIndex++)] = 50;
            mCmdBuffer[Code128C] = ((byte) (num + 4 + transNum));
        } else if (checkcodeID == 98) {
            mCmdBuffer[(mIndex++)] = 123;
            mCmdBuffer[(mIndex++)] = 83;
            mCmdBuffer[Code128C] = ((byte) (num + 4 + transNum));
        } else if (checkcodeID == 99) {
            mCmdBuffer[(mIndex++)] = 123;
            mCmdBuffer[(mIndex++)] = 67;
            mCmdBuffer[Code128C] = ((byte) (num + 4 + transNum));
        } else if (checkcodeID == 100) {
            mCmdBuffer[(mIndex++)] = 123;
            mCmdBuffer[(mIndex++)] = 52;
            mCmdBuffer[Code128C] = ((byte) (num + 4 + transNum));
        } else if (checkcodeID == 101) {
            mCmdBuffer[(mIndex++)] = 123;
            mCmdBuffer[(mIndex++)] = 65;
            mCmdBuffer[Code128C] = ((byte) (num + 4 + transNum));
        } else if (checkcodeID == 102) {
            mCmdBuffer[(mIndex++)] = 123;
            mCmdBuffer[(mIndex++)] = 49;
            mCmdBuffer[Code128C] = ((byte) (num + 4 + transNum));
        }
    }

    public static boolean SPPWrite(byte[] Data, int DataLen) {
        try {
            mWifiOutputStream.write(Data, 0, DataLen);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean SPPWrite(byte[] buffer) {
        try {
            mWifiOutputStream.write(buffer);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static void printString(String str) {
        try {
            SPPWrite(str.getBytes(encode));
            SPPWrite(new byte[]{10});
        } catch (UnsupportedEncodingException e) {

        }
    }

    public static void printParameterSet(byte[] buf) {
        SPPWrite(buf);
    }

    public static void printByteData(byte[] buf) {
        SPPWrite(buf);
        SPPWrite(new byte[]{10});
    }

    public static void printImage() {
        printParameterSet(new byte[]{27, 64});
        printParameterSet(new byte[]{27, 33});

        byte[] bufTemp2 = {};

        printByteData(bufTemp2);
        printString("");

        printParameterSet(new byte[]{27, 64});
        printParameterSet(new byte[]{27, 97});
    }

}
