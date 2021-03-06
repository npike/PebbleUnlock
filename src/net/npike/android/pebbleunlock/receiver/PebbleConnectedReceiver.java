package net.npike.android.pebbleunlock.receiver;

import net.npike.android.pebbleunlock.BuildConfig;
import net.npike.android.pebbleunlock.R;
import net.npike.android.pebbleunlock.activity.PrefActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class PebbleConnectedReceiver extends PebbleUnlockReceiver {
	private static final String TAG = "PebbleConnectionReceiver";

	@Override
	public boolean onPebbleAction(Context context, String pebbleAddress) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Pebble " + pebbleAddress + " connected.");
		}

		resetPassword(context, "");

		long when = System.currentTimeMillis();

		// Package intent with Alert string data set.
		Intent notificationIntent = new Intent(context, PrefActivity.class);
		notificationIntent.setAction(Intent.ACTION_VIEW);

		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context)
				.setContentTitle(context.getString(R.string.app_name))
				.setContentText(
						context.getString(R.string.notification_pebble_connected_text))
				.setWhen(when).setContentIntent(pendingIntent)
				.setSmallIcon(R.drawable.ic_unlocked);

		builder.setOngoing(true);
		builder.setPriority(NotificationCompat.PRIORITY_MIN);

		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		bigTextStyle.setBigContentTitle(context.getString(R.string.app_name));
		bigTextStyle.bigText(context
				.getString(R.string.notification_pebble_connected_text));
		builder.setStyle(bigTextStyle);

		Notification notification = builder.build();

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(0, notification);
		
		return true;
	}

}