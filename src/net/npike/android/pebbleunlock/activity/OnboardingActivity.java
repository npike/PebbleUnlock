package net.npike.android.pebbleunlock.activity;

import net.npike.android.pebbleunlock.OnboardingInterface;
import net.npike.android.pebbleunlock.R;
import net.npike.android.pebbleunlock.fragment.OnboardingConfigurePasswordFragment;
import net.npike.android.pebbleunlock.fragment.OnboardingRequestDeviceAdminFragment;
import net.npike.android.pebbleunlock.fragment.OnboardingWaitForPebbleAddress;
import android.app.Activity;
import android.os.Bundle;

public class OnboardingActivity extends Activity implements OnboardingInterface {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_onboarding);

		if (savedInstanceState == null) {
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.fragment_placeholder,
							OnboardingWaitForPebbleAddress.getInstance(),
							"wait_for_pebble_frag").commit();
		}
	}

	@Override
	public void onPasswordConfigured() {
		getFragmentManager()
				.beginTransaction()
				.replace(R.id.fragment_placeholder,
						OnboardingRequestDeviceAdminFragment.getInstance(),
						"request_admin_frag").commit();
	}

	@Override
	public void onPebbleFound(String address) {
		getFragmentManager()
				.beginTransaction()
				.replace(R.id.fragment_placeholder,
						OnboardingConfigurePasswordFragment.getInstance(),
						"configure_password_frag").commit();
	}

}
