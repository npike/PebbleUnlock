package net.npike.android.pebbleunlock.fragment;

import net.npike.android.pebbleunlock.PebbleUnlockApp;
import net.npike.android.pebbleunlock.R;
import net.npike.android.pebbleunlock.receiver.PebbleUnlockDeviceAdminReceiver;
import android.app.Fragment;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class OnboardingRequestDeviceAdminFragment extends Fragment implements OnClickListener {
	private static final int REQUEST_CODE_ENABLE_ADMIN = 1;
	private Button mButtonRequestAdmin;
	private ComponentName mDeviceAdminSample;

	public static OnboardingRequestDeviceAdminFragment getInstance() {
		return new OnboardingRequestDeviceAdminFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.frag_onboarding_request_deviceadmin,
				container, false);

		mButtonRequestAdmin = (Button) v.findViewById(R.id.buttonRequestAdmin);
		mButtonRequestAdmin.setOnClickListener(this);
 
		mDeviceAdminSample = new ComponentName(getActivity(),
				PebbleUnlockDeviceAdminReceiver.class);

		return v;
	}

	@Override
	public void onClick(View v) {
		// Launch the activity to have the user enable our admin.
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
				mDeviceAdminSample);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
				getString(R.string.add_admin_extra_app_text));
		startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
			if (resultCode == -1) {
				PebbleUnlockApp.getInstance().setEnabled(true);
				getActivity().finish();
			} else {
				Toast.makeText(getActivity(), "You must enable admin access to use Pebble Unlock", Toast.LENGTH_SHORT).show();
			}
		}
	}

}
