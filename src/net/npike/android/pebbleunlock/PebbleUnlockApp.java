package net.npike.android.pebbleunlock;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PebbleUnlockApp extends Application {
	private static PebbleUnlockApp INSTANCE;
	private SharedPreferences mPrefs;

	@Override
	public void onCreate() {
		super.onCreate();

		INSTANCE = this;
		
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

	}
	
	
	public static PebbleUnlockApp getInstance() {
		return INSTANCE;
	}
	
	public boolean isEnabled() {
		return mPrefs.getBoolean(getString(R.string.pref_key_enable), false);
	}

	public void setEnabled(boolean enabled) {
		mPrefs.edit().putBoolean(getString(R.string.pref_key_enable), enabled).commit();
	}
	
	public String getPassword() {
		return mPrefs.getString(getString(R.string.pref_key_password), "");
	}
}
