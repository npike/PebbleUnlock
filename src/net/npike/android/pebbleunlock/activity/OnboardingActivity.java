package net.npike.android.pebbleunlock.activity;

import net.npike.android.pebbleunlock.OnboardingInterface;
import net.npike.android.pebbleunlock.R;
import net.npike.android.pebbleunlock.fragment.OnboardingConfigurePasswordFragment;
import net.npike.android.pebbleunlock.fragment.OnboardingRequestDeviceAdminFragment;
import net.npike.android.pebbleunlock.fragment.OnboardingWaitForPebbleAddressFragment;
import android.app.Activity;
import android.os.Bundle;

public class OnboardingActivity extends Activity implements OnboardingInterface {

	private static final String TAG_CONFIGURE_PASSWORD_FRAG = "configure_password_frag";
	private static final String TAG_REQUEST_ADMIN_FRAG = "request_admin_frag";
	private static final String TAG_WAIT_FOR_PEBBLE_FRAG = "wait_for_pebble_frag";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_onboarding);

		if (savedInstanceState == null) {
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.fragment_placeholder,
							OnboardingWaitForPebbleAddressFragment.getInstance(),
							TAG_WAIT_FOR_PEBBLE_FRAG).commit();
		}
	}

	@Override
	public void onPasswordConfigured() {
		getFragmentManager()
				.beginTransaction()
				.replace(R.id.fragment_placeholder,
						OnboardingRequestDeviceAdminFragment.getInstance(),
						TAG_REQUEST_ADMIN_FRAG).commit();
	}

	@Override
	public void onPebbleFound(String address) {
		getFragmentManager()
				.beginTransaction()
				.replace(R.id.fragment_placeholder,
						OnboardingConfigurePasswordFragment.getInstance(),
						TAG_CONFIGURE_PASSWORD_FRAG).commit();
	}

}
