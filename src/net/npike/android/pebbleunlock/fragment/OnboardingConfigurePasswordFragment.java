package net.npike.android.pebbleunlock.fragment;

import net.npike.android.pebbleunlock.OnboardingInterface;
import net.npike.android.pebbleunlock.PebbleUnlockApp;
import net.npike.android.pebbleunlock.R;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class OnboardingConfigurePasswordFragment extends PasswordChangeFragment
		implements OnClickListener {

	private Button mButtonFinished;
	private TextView mTextViewPebbleFound;

	public static OnboardingConfigurePasswordFragment getInstance() {
		return new OnboardingConfigurePasswordFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.frag_onboarding_configure_password, null, false);

		mTextViewPebbleFound = (TextView) view
				.findViewById(R.id.textViewPebbleFound);
		mButtonFinished = (Button) view.findViewById(R.id.buttonFinish);
		mButtonFinished.setOnClickListener(this);

		CharSequence foo = setSpanBetweenTokens(
				getString(R.string.oboarding_password_description,
						PebbleUnlockApp.getInstance().getPairedPebbleAddress()),
				"##",
				new ForegroundColorSpan(getResources().getColor(
						R.color.pebble_red)));
		mTextViewPebbleFound.setText(foo);

		bindView(view);

		return view;
	}

	@Override
	protected void onPasswordChange(boolean confirmed) {
		mButtonFinished.setEnabled(confirmed);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.buttonFinish) {
			// commit password and then start admin activity
			handlePasswordConfirm();

			try {
				((OnboardingInterface) getActivity()).onPasswordConfigured();
			} catch (ClassCastException ccex) {

			}
		}

	}

	/**
	 * Given either a Spannable String or a regular String and a token, apply
	 * the given CharacterStyle to the span between the tokens, and also remove
	 * tokens.
	 * <p>
	 * For example, {@code setSpanBetweenTokens("Hello ##world##!", "##",
	 * new ForegroundColorSpan(0xFFFF0000));} will return a CharSequence
	 * {@code "Hello world!"} with {@code world} in red.
	 * 
	 * @param text
	 *            The text, with the tokens, to adjust.
	 * @param token
	 *            The token string; there should be at least two instances of
	 *            token in text.
	 * @param cs
	 *            The style to apply to the CharSequence. WARNING: You cannot
	 *            send the same two instances of this parameter, otherwise the
	 *            second call will remove the original span.
	 * @return A Spannable CharSequence with the new style applied.
	 * 
	 * @see http 
	 *      ://developer.android.com/reference/android/text/style/CharacterStyle
	 *      .html
	 */
	public static CharSequence setSpanBetweenTokens(CharSequence text,
			String token, CharacterStyle... cs) {
		// Start and end refer to the points where the span will apply
		int tokenLen = token.length();
		int start = text.toString().indexOf(token) + tokenLen;
		int end = text.toString().indexOf(token, start);

		if (start > -1 && end > -1) {
			// Copy the spannable string to a mutable spannable string
			SpannableStringBuilder ssb = new SpannableStringBuilder(text);
			for (CharacterStyle c : cs)
				ssb.setSpan(c, start, end, 0);

			// Delete the tokens before and after the span
			ssb.delete(end, end + tokenLen);
			ssb.delete(start - tokenLen, start);

			text = ssb;
		}

		return text;
	}
}
