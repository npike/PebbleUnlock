package net.npike.android.pebbleunlock.receiver;

import net.npike.android.pebbleunlock.BuildConfig;
import net.npike.android.pebbleunlock.PebbleUnlockApp;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public abstract class PebbleUnlockReceiver extends BroadcastReceiver {

	private static final String EXTRA_PEBBLE_ADDRESS = "address";
	private static final String TAG = "PebbleUnlockReceiver";
	public static final String EXTRA_LOST_CONNECTION = "LOST_CONNECTION";
	
	protected DevicePolicyManager mDevicePolicyManager;
	private Intent mIntent;

	@Override
	final public void onReceive(Context context, Intent intent) {
		// Prepare to work with the DPM
		mDevicePolicyManager = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		mIntent = intent;
		
		final String pebbleAddress = intent
				.getStringExtra(EXTRA_PEBBLE_ADDRESS);

		if (PebbleUnlockApp.getInstance().isEnabled()) {
			onPebbleAction(context, pebbleAddress);
		} else {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Pebble Unlock is not enabled.");
			}
		}
	}
	
	protected Intent getIntent() {
		return mIntent;
	}

	public abstract void onPebbleAction(Context context, String pebbleAddress);

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
