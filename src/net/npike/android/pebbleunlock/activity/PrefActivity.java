package net.npike.android.pebbleunlock.activity;

import net.npike.android.pebbleunlock.BuildConfig;
import net.npike.android.pebbleunlock.PebbleUnlockApp;
import net.npike.android.pebbleunlock.R;
import net.npike.android.pebbleunlock.receiver.DeviceAdminSampleReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.util.Log;

public class PrefActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	private OnPreferenceChangeListener mOnPreferenceChangedListenerEnabled = new OnPreferenceChangeListener() {

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if (mIgnoreNextEnableRequest) {
				mIgnoreNextEnableRequest = false;
				return true;
			}
			if ((Boolean) newValue) {
				requestAdmin();
			} else {
				mDPM.removeActiveAdmin(mDeviceAdminSample);
				return true;
			}
			return false;
		}
	};

	private static final String TAG = "PrefActivity";
	private static final int REQUEST_CODE_ENABLE_ADMIN = 1;
	private DevicePolicyManager mDPM;
	private ComponentName mDeviceAdminSample;
	private SwitchPreference mSwitchPreferenceEnable;
	private boolean mIgnoreNextEnableRequest = false;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Prepare to work with the DPM
		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mDeviceAdminSample = new ComponentName(this,
				DeviceAdminSampleReceiver.class);

		addPreferencesFromResource(R.xml.preferences);

		Preference version = getPreferenceManager().findPreference("version");

		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			String versionString = pInfo.versionName + " (Build "
					+ pInfo.versionCode + ")";
			version.setSummary(versionString);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		mSwitchPreferenceEnable = (SwitchPreference) getPreferenceManager()
				.findPreference(getString(R.string.pref_key_enable));

		mSwitchPreferenceEnable
				.setOnPreferenceChangeListener(mOnPreferenceChangedListenerEnabled);

		getPreferenceManager().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
			Log.d(TAG, "resultCode: " + resultCode);
			if (resultCode == -1) {
				Log.d(TAG, "should be making this fucker checked.");
				mIgnoreNextEnableRequest = true;
				mSwitchPreferenceEnable.setChecked(true);
			}
		}
		// super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "onSharedPreferenceChanged " + key);
		}
		// if (key.equalsIgnoreCase(getString(R.string.pref_key_enable))) {
		// if (PebbleUnlockApp.getInstance().isEnabled()) {
		// requestAdmin();
		// }
		// }
	}

	private void requestAdmin() {
		// Launch the activity to have the user enable our admin.
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
				mDeviceAdminSample);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
				getString(R.string.add_admin_extra_app_text));
		startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
	}
}
