package net.npike.android.pebbleunlock.receiver;

import net.npike.android.pebbleunlock.PebbleUnlockApp;
import net.npike.android.pebbleunlock.R;
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Sample implementation of a DeviceAdminReceiver. Your controller must provide
 * one, although you may or may not implement all of the methods shown here.
 * 
 * All callbacks are on the UI thread and your implementations should not engage
 * in any blocking operations, including disk I/O.
 */
public class PebbleUnlockDeviceAdminReceiver extends DeviceAdminReceiver {

	void showToast(Context context, String msg) {
		//String status = context.getString(R.string.admin_receiver_status, msg);
		//Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onEnabled(Context context, Intent intent) {
		showToast(context,
				context.getString(R.string.admin_receiver_status_enabled));
		PebbleUnlockApp.getInstance().setEnabled(true);
	}

	@Override
	public CharSequence onDisableRequested(Context context, Intent intent) {
		return context
				.getString(R.string.admin_receiver_status_disable_warning);
	}

	@Override
	public void onDisabled(Context context, Intent intent) {
		showToast(context,
				context.getString(R.string.admin_receiver_status_disabled));
		PebbleUnlockApp.getInstance().setEnabled(false);
	}

	@Override
	public void onPasswordChanged(Context context, Intent intent) {
		showToast(context,
				context.getString(R.string.admin_receiver_status_pw_changed));
	}

	@Override
	public void onPasswordFailed(Context context, Intent intent) {
		showToast(context,
				context.getString(R.string.admin_receiver_status_pw_failed));
	}

	@Override
	public void onPasswordSucceeded(Context context, Intent intent) {
		showToast(context,
				context.getString(R.string.admin_receiver_status_pw_succeeded));
	}

	@Override
	public void onPasswordExpiring(Context context, Intent intent) {
		
	}
}