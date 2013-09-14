package net.npike.android.pebbleunlock.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public abstract class PebbleUnlockReceiver extends BroadcastReceiver {

	private static final String EXTRA_PEBBLE_ADDRESS = "address";
	protected DevicePolicyManager mDPM;

	// private ComponentName mDeviceAdminSample;

	@Override
	final public void onReceive(Context context, Intent intent) {
		// Prepare to work with the DPM
		mDPM = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		// mDeviceAdminSample = new ComponentName(context,
		// DeviceAdminSampleReceiver.class);
		final String pebbleAddress = intent
				.getStringExtra(EXTRA_PEBBLE_ADDRESS);

		onPebbleAction(context, pebbleAddress);
	}

	public abstract void onPebbleAction(Context context, String pebbleAddress);

	protected void resetPassword(Context context, String newPassword) {
		boolean result = mDPM.resetPassword(newPassword,
				DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
		if (result) {
			Toast.makeText(context,
					"Password changed to '" + newPassword + "'",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "Couldn't reset password.",
					Toast.LENGTH_SHORT).show();
		}
	}

}
