package net.npike.android.pebbleunlock.receiver;

import net.npike.android.pebbleunlock.BuildConfig;
import net.npike.android.pebbleunlock.PebbleUnlockApp;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
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
			// is this the same address we captured during onboarding?
			if (TextUtils.equals(pebbleAddress, PebbleUnlockApp.getInstance().getPairedPebbleAddress())) {
				onPebbleAction(context, pebbleAddress);
			} else {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Address "+pebbleAddress +" does not match address from onboard: "+PebbleUnlockApp.getInstance().getPairedPebbleAddress());
				}
			}
		} else {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Pebble Unlock is not enabled.");
			}
		}
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
