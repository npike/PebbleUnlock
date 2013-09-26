package net.npike.android.pebbleunlock.fragment;

import net.npike.android.pebbleunlock.OnboardingInterface;
import net.npike.android.pebbleunlock.PebbleUnlockApp;
import net.npike.android.pebbleunlock.R;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

public class OnboardingWaitForPebbleAddressFragment extends Fragment {
	private static final String EXTRA_PEBBLE_ADDRESS = "address";
	private BroadcastReceiver mReceiver;

	public static OnboardingWaitForPebbleAddressFragment getInstance() {
		return new OnboardingWaitForPebbleAddressFragment();
	}

	@Override
	public void onResume() {
		super.onResume();

		IntentFilter filter = new IntentFilter();
		filter.addAction("com.getpebble.action.PEBBLE_CONNECTED");
		filter.addAction("com.getpebble.action.PEBBLE_DISCONNECTED");

		mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				final String pebbleAddress = intent
						.getStringExtra(EXTRA_PEBBLE_ADDRESS); 

				PebbleUnlockApp.getInstance().putPairedPebbleAddress(
						pebbleAddress);

				try {
					((OnboardingInterface) getActivity())
							.onPebbleFound(pebbleAddress);
				} catch (ClassCastException ccex) {

				}
			}
		};
		getActivity().registerReceiver(mReceiver, filter);

	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			getActivity().unregisterReceiver(mReceiver);
		} catch (IllegalArgumentException iae) {

		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.frag_onboarding_waitforpebbleaddress,
						container, false);
		ProgressBar progressBar = (ProgressBar) v
				.findViewById(R.id.progressBar);

		progressBar.setIndeterminate(true);

		return v;
	}
}
