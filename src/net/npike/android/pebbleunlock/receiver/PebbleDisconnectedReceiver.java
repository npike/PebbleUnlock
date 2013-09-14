package net.npike.android.pebbleunlock.receiver;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

public class PebbleDisconnectedReceiver extends PebbleUnlockReceiver {
	private static final String TAG = "PebbleDisconnectedReceiver";

	@Override
	public void onPebbleAction(Context context, String pebbleAddress) {
		Log.i(TAG, "Pebble disconnected " + pebbleAddress);

		resetPassword(context, "7873");
		
		
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(0);
	}

}