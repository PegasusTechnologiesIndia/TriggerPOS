package org.phomellolitepos.Util;
import java.io.PrintWriter;
import java.io.StringWriter;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.app.AlertDialog;
import android.view.WindowManager;

import org.phomellolitepos.SendLogActivity;

public class ExceptionHandler implements
		java.lang.Thread.UncaughtExceptionHandler {
	private static Activity myContext=null;
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private AlertDialog dialog;

	public ExceptionHandler(Activity context) {
		myContext = context;
	}
	public ExceptionHandler() {}
	@SuppressWarnings("deprecation")
	public void uncaughtException(Thread thread, Throwable exception) {
		prepareLogs(exception);
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(10);
	}

	public static void prepareLogs(Throwable exception){
		StringWriter stackTrace = new StringWriter();
		exception.printStackTrace(new PrintWriter(stackTrace));
		StringBuilder errorReport = new StringBuilder();
		errorReport.append("************ CAUSE OF ERROR ************" + LINE_SEPARATOR);
		errorReport.append(stackTrace.toString());

		Long tsLong = System.currentTimeMillis()/1000;
		String ts = tsLong.toString();
		errorReport.append( "************ Timestamp ************" + ts);
		errorReport.append(LINE_SEPARATOR+ "************ DEVICE INFORMATION ***********" + LINE_SEPARATOR);
		errorReport.append("Brand: "+Build.BRAND);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Device: "+Build.DEVICE);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Model: "+Build.MODEL);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Id: "+Build.ID);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Product: "+Build.PRODUCT);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Serial: "+Build.SERIAL);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append(LINE_SEPARATOR + "************ BUILD INFO ************" + LINE_SEPARATOR);
		errorReport.append("SDK: "+Build.VERSION.SDK);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Release: "+Build.VERSION.RELEASE);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Incremental: "+Build.VERSION.INCREMENTAL);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append(LINE_SEPARATOR + "************ CUSTOMER INFO ************" + LINE_SEPARATOR);
		errorReport.append("Company Name: "+Globals.objLPR.getCompany_Name());
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Contact Person: "+Globals.objLPR.getContact_Person());
		errorReport.append(LINE_SEPARATOR);
		reportLogs(errorReport.toString());
	}

	public static void reportLogs(String errorLogs) {
		Intent intent = new Intent(myContext, SendLogActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
		intent.putExtra("logs", errorLogs.toString());
		myContext.startActivity(intent);
		myContext.finish();
	}
}