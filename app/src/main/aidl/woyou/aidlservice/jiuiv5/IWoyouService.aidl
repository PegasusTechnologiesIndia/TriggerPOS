

package woyou.aidlservice.jiuiv5;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.ILcdCallback;
import android.graphics.Bitmap;
import woyou.aidlservice.jiuiv5.ITax;

interface IWoyouService
{

	void updateFirmware();


	int getFirmwareStatus();


	String getServiceVersion();


	void printerInit(in ICallback callback);


	void printerSelfChecking(in ICallback callback);


	String getPrinterSerialNo();


	String getPrinterVersion();


	String getPrinterModal();


	int getPrintedLength();


	void lineWrap(int n, in ICallback callback);


	void sendRAWData(in byte[] data, in ICallback callback);


	void setAlignment(int alignment, in ICallback callback);


	void setFontName(String typeface, in ICallback callback);


	void setFontSize(float fontsize, in ICallback callback);


	void printText(String text, in ICallback callback);


	void printTextWithFont(String text, String typeface, float fontsize, in ICallback callback);


	void printColumnsText(in String[] colsTextArr, in int[] colsWidthArr, in int[] colsAlign, in ICallback callback);



	void printBitmap(in Bitmap bitmap, in ICallback callback);

	/**

	*    0 -- UPC-A，
	*    1 -- UPC-E，
	*    2 -- JAN13(EAN13)，
	*    3 -- JAN8(EAN8)，
	*    4 -- CODE39，
	*    5 -- ITF，
	*    6 -- CODABAR，
	*    7 -- CODE93，
	*    8 -- CODE128

	*/
	void printBarCode(String data, int symbology, int height, int width, int textposition,  in ICallback callback);


	void printQRCode(String data, int modulesize, int errorlevel, in ICallback callback);


	void printOriginalText(String text, in ICallback callback);


	void commitPrinterBuffer();


	void cutPaper(in ICallback callback);


	int getCutPaperTimes();


	void openDrawer(in ICallback callback);


	int getOpenDrawerTimes();


	void enterPrinterBuffer(in boolean clean);


	void exitPrinterBuffer(in boolean commit);

	void tax(in byte [] data,in ITax callback);

	int getPrinterMode();


	int getPrinterBBMDistance();


	void printColumnsString(in String[] colsTextArr, in int[] colsWidthArr, in int[] colsAlign, in ICallback callback);


	int updatePrinterState();


	void sendLCDCommand(in int flag);

	void sendLCDString(in String string, ILcdCallback callback);

	void sendLCDBitmap(in Bitmap bitmap, ILcdCallback callback);


    	void commitPrinterBufferWithCallback(in ICallback callback);


    	void exitPrinterBufferWithCallback(in boolean commit, in ICallback callback);

    	void sendLCDDoubleString(in String topText, in String bottomText, ILcdCallback callback);


        void printBitmapCustom(in Bitmap bitmap, in int type, in ICallback callback);
}