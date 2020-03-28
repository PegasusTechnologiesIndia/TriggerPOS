/**
* Ipos 打印服务回调
* IPosPrinterCallback.aidl
* AIDL Version：1.0.0
*/
package com.iposprinter.iposprinterservice;


interface IPosPrinterCallback {


	oneway void onRunResult(boolean isSuccess);


	oneway void onReturnString(String result);
}
