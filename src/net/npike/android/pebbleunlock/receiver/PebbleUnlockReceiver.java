package net.npike.android.pebbleunlock.receiver;

import net.npike.android.pebbleunlock.BuildConfig;
import net.npike.android.pebbleunlock.PebbleUnlockApp;
import net.npike.android.pebbleunlock.R;
import net.npike.android.pebbleunlock.provider.LogContract;
import android.app.admin.DevicePolicyManager;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public abstract class PebbleUnlockReceiver extends BroadcastReceiver {

	private static final String EXTRA_PEBBLE_ADDRESS = "address";
	private static final String TAG = "PebbleUnlockReceiver";
	protected DevicePolicyManager mDevicePolicyManager;

	@Override
	final public void onReceive(Context context, Intent intent) {
		// Prepare to work with the DPM
		mDevicePolicyManager = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		final String pebbleAddress = intent
				.getStringExtra(EXTRA_PEBBLE_ADDRESS);

		if (PebbleUnlockApp.getInstance().isEnabled()) {
			boolean isConnected = onPebbleAction(context, pebbleAddress);

			AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(
					context.getContentResolver()) {
			};

			ContentValues cv = new ContentValues();
			cv.put(LogContract.ConnectionEvent.COLUMN_NAME_CONNECTED,
					isConnected ? 1 : 0);
			cv.put(LogContract.ConnectionEvent.COLUMN_NAME_TIME,
					System.currentTimeMillis()); 
			asyncQueryHandler.startInsert(0, null,
					LogContract.ConnectionEvent.CONTENT_URI, cv);

		} else {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Pebble Unlock is not enabled.");
			}
		}
	}

	public abstract boolean onPebbleAction(Context context, String pebbleAddress);

	protected void resetPassword(Context context, String newPassword) {

		boolean result = mDevicePolicyManager.resetPassword(newPassword,
				DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);

		if (BuildConfig.DEBUG) {
			if (result) {
				Log.d(TAG, "password changed.");
			} else {
				Log.d(TAG, "password not changed.");
			}
		}
	}
}
