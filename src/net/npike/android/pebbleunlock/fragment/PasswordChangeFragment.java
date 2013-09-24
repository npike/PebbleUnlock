package net.npike.android.pebbleunlock.fragment;

import net.npike.android.pebbleunlock.PebbleUnlockApp;
import net.npike.android.pebbleunlock.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordChangeFragment extends DialogFragment {

	public static PasswordChangeFragment getInstance() {
		return new PasswordChangeFragment();
	}

	private EditText mEditTextPassword;
	private EditText mEditTextPasswordConfirm;
	private TextView mTextViewPasswordStatus;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		View view = inflater
				.inflate(R.layout.frag_password_change, null, false);
		mEditTextPassword = (EditText) view.findViewById(R.id.editTextPassword);
		mEditTextPasswordConfirm = (EditText) view
				.findViewById(R.id.editTextPasswordConfirm);
		mTextViewPasswordStatus = (TextView) view
				.findViewById(R.id.textViewPasswordStatus);

		mEditTextPasswordConfirm.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				if (mEditTextPassword.getText().toString()
						.equals(mEditTextPasswordConfirm.getText().toString())) {
					mTextViewPasswordStatus.setText("");
				} else {
					mTextViewPasswordStatus.setText(R.string.dialog_change_passwords_do_not_match);

				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// NOOP

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// NOOP

			}

		});

		builder.setView(view);
		builder.setTitle(R.string.dialog_change_set_password);
		builder.setPositiveButton(R.string.dialog_change_change, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do the passwords actually match?
				if (mEditTextPassword.getText().toString()
						.equals(mEditTextPasswordConfirm.getText().toString())
						&& !TextUtils.isEmpty(mEditTextPassword.getText()
								.toString())
						&& !TextUtils.isEmpty(mEditTextPasswordConfirm
								.getText().toString())) {
					PebbleUnlockApp.getInstance().setPassword(
							mEditTextPassword.getText().toString());
				} else {
					Toast.makeText(getActivity(), R.string.dialog_change_password_not_changed,
							Toast.LENGTH_SHORT).show();
				}

			}
		});
		builder.setNegativeButton(R.string.dialog_change_cancel, null);

		return builder.create();
	}
}
