package org.phomellolitepos.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.phomellolitepos.SplashScreen;

public class BootStartUpReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent App = new Intent(context, SplashScreen.class);
		App.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(App);

	}
}
