package net.npike.android.pebbleunlock.receiver;

import net.npike.android.pebbleunlock.PebbleUnlockApp;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class PebbleUnlockReceiver extends BroadcastReceiver {

	private static final String EXTRA_PEBBLE_ADDRESS = "address";
	protected DevicePolicyManager mDevicePolicyManager;

	@Override
	final public void onReceive(Context context, Intent intent) {
		// Prepare to work with the DPM
		mDevicePolicyManager = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		final String pebbleAddress = intent
				.getStringExtra(EXTRA_PEBBLE_ADDRESS);

		if (PebbleUnlockApp.getInstance().isEnabled()) {
			onPebbleAction(context, pebbleAddress);
		}
	}

	public abstract void onPebbleAction(Context context, String pebbleAddress);

	protected void resetPassword(Context context, String newPassword) {
		mDevicePolicyManager.resetPassword(newPassword,
				DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
	}

}
