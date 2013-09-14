package net.npike.android.pebbleunlock.activity;

import net.npike.android.pebbleunlock.R;
import net.npike.android.pebbleunlock.R.id;
import net.npike.android.pebbleunlock.R.layout;
import net.npike.android.pebbleunlock.R.menu;
import net.npike.android.pebbleunlock.R.string;
import net.npike.android.pebbleunlock.receiver.DeviceAdminSampleReceiver;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	private static final int REQUEST_CODE_ENABLE_ADMIN = 1;

	protected DevicePolicyManager mDPM;
	private ComponentName mDeviceAdminSample;
	private Button mButtonLock;
	private Button mButtonUnlock;
	private EditText mEditTextPassword;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Prepare to work with the DPM
		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mDeviceAdminSample = new ComponentName(this,
				DeviceAdminSampleReceiver.class);

		// Launch the activity to have the user enable our admin.
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
				mDeviceAdminSample);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
				getString(R.string.add_admin_extra_app_text));
		startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);

		mButtonLock = (Button) findViewById(R.id.buttonLock);
		mButtonUnlock = (Button) findViewById(R.id.buttonUnlock);
		mEditTextPassword = (EditText) findViewById(R.id.editTextPassword);

		mButtonLock.setOnClickListener(this);
		mButtonUnlock.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonLock:
			doPassword(mEditTextPassword.getText().toString());
			break;
		case R.id.buttonUnlock:
			doPassword("");
			break;
		}
	}

	private void doPassword(String newPassword) {
		boolean result = mDPM.resetPassword(newPassword,
				DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
		if (result) {
			Toast.makeText(this, "Password changed to '" + newPassword + "'",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Couldn't reset password.", Toast.LENGTH_SHORT).show();
		}
	}

}
