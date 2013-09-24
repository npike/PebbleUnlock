package net.npike.android.pebbleunlock.activity;

import net.npike.android.pebbleunlock.BuildConfig;
import net.npike.android.pebbleunlock.PebbleUnlockApp;
import net.npike.android.pebbleunlock.R;
import net.npike.android.pebbleunlock.fragment.PasswordChangeFragment;
import net.npike.android.pebbleunlock.receiver.PebbleUnlockDeviceAdminReceiver;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.Log;
import android.widget.Toast;

public class PrefActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener, OnPreferenceClickListener {

	private static final String TAG_PASSWORD_CHANGE_FRAG = "password_change_frag";

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

	private PreferenceScreen mSetPassword;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Prepare to work with the DPM
		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mDeviceAdminSample = new ComponentName(this,
				PebbleUnlockDeviceAdminReceiver.class);

		addPreferencesFromResource(R.xml.preferences);

		Preference version = getPreferenceManager().findPreference(
				getString(R.string.pref_key_version));

		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			version.setSummary(getString(R.string.pref_version_summary,
					packageInfo.versionName, packageInfo.versionCode));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		mSwitchPreferenceEnable = (SwitchPreference) getPreferenceManager()
				.findPreference(getString(R.string.pref_key_enable));
		mSwitchPreferenceEnable
				.setOnPreferenceChangeListener(mOnPreferenceChangedListenerEnabled);
		
		mSetPassword = (PreferenceScreen) findPreference("key_set_password");
		mSetPassword.setOnPreferenceClickListener(this);
		

		getPreferenceManager().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
			if (resultCode == -1) {
				mIgnoreNextEnableRequest = true;
				mSwitchPreferenceEnable.setChecked(true);
			}
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "onSharedPreferenceChanged " + key);
		}
		if (key.equalsIgnoreCase(getString(R.string.pref_key_password))) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.dialog_password_apply_now_message);
			builder.setPositiveButton(R.string.dialog_password_apply_now_yes, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					boolean result = mDPM.resetPassword(PebbleUnlockApp
							.getInstance().getPassword(),
							DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
					if (result) {
						Toast.makeText(PrefActivity.this, R.string.dialog_password_apply_now_password_changed,
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(PrefActivity.this,
								R.string.dialog_password_apply_now_couldn_t_reset_password, Toast.LENGTH_SHORT)
								.show();
					}

				}
			});
			builder.setNegativeButton(R.string.dialog_password_apply_now_no, null);
			builder.show();
		}
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

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference == mSetPassword) {
			PasswordChangeFragment.getInstance().show(getFragmentManager(), TAG_PASSWORD_CHANGE_FRAG);
		}
		return false;
	}
}
