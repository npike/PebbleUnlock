package net.npike.android.pebbleunlock.receiver;

import net.npike.android.pebbleunlock.BuildConfig;
import net.npike.android.pebbleunlock.PebbleUnlockApp;
import android.app.admin.DevicePolicyManager;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public abstract class PebbleUnlockReceiver extends BroadcastReceiver {

	private static final String EXTRA_PEBBLE_ADDRESS = "address";
	private static final String TAG = "PebbleUnlockReceiver";
	public static final String EXTRA_LOST_CONNECTION = "LOST_CONNECTION";

	protected DevicePolicyManager mDevicePolicyManager;
	private Intent mIntent;
	private boolean mLastResult;

	@Override
	final public void onReceive(Context context, Intent intent) {
		// Prepare to work with the DPM
		mDevicePolicyManager = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		mIntent = intent;

		final String pebbleAddress = intent
				.getStringExtra(EXTRA_PEBBLE_ADDRESS);

		if (PebbleUnlockApp.getInstance().isEnabled()) {

			// is this the same address we captured during onboarding?
			if (TextUtils.equals(pebbleAddress, PebbleUnlockApp.getInstance()
					.getPairedPebbleAddress())) {
				boolean isConnected = onPebbleAction(context, pebbleAddress);

				String message = null;
				if (getIntent().hasExtra(EXTRA_LOST_CONNECTION)) {
					message = "Loss of connection detected.";
				}

				logMessage(context, isConnected, message);

				if (mLastResult) {
					logMessage(context, false, "Password updated.");
				} else {
					logMessage(context, false, "Failed to set password.");
				}
			} else {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Address "
							+ pebbleAddress
							+ " does not match address from onboard: "
							+ PebbleUnlockApp.getInstance()
									.getPairedPebbleAddress());
				}
			}

		} else {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Pebble Unlock is not enabled.");
			}
		}
	}

	private void logMessage(Context context, boolean isConnected, String message) {
		AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(
				context.getContentResolver()) {
		};

		ContentValues cv = new ContentValues();
		cv.put(LogContract.ConnectionEvent.COLUMN_NAME_CONNECTED,
				isConnected ? 1 : 0);
		cv.put(LogContract.ConnectionEvent.COLUMN_NAME_TIME,
				System.currentTimeMillis());
		if (!TextUtils.isEmpty(message)) {
			cv.put(LogContract.ConnectionEvent.COLUMN_NAME_MESSAGE, message);
		}
		asyncQueryHandler.startInsert(0, null,
				LogContract.ConnectionEvent.CONTENT_URI, cv);
	}

	protected Intent getIntent() {
		return mIntent;
	}

	public abstract boolean onPebbleAction(Context context, String pebbleAddress);

	protected void resetPassword(Context context, String newPassword) {

		mLastResult = mDevicePolicyManager.resetPassword(newPassword,
				DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);

		if (mLastResult) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "password changed.");
			}
		} else {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "password not changed.");
			}
		}
	}
}
