package net.npike.android.pebbleunlock;

public interface OnboardingInterface {
	public abstract void onPebbleFound(String address);
	public abstract void onPasswordConfigured();
}
