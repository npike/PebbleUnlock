package net.npike.android.pebbleunlock.activity;

import net.npike.android.pebbleunlock.R;
import net.npike.android.pebbleunlock.provider.LogContract;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LogActivity extends Activity implements LoaderCallbacks<Cursor> {

	private ListView mListView;
	private ConnectionEventAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log);

		mListView = (ListView) findViewById(R.id.listView);
		mListView.setEmptyView(findViewById(android.R.id.empty));
		mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		mAdapter = new ConnectionEventAdapter(this,
				R.layout.item_connection_event);
		mListView.setAdapter(mAdapter);

		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(this, LogContract.ConnectionEvent.CONTENT_URI,
				null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursor) {
		// NOOP
	}

	class ConnectionEventAdapter extends CursorAdapter {

		private LayoutInflater inflater;
		private int mListItemResId;

		public ConnectionEventAdapter(final Context context,
				final int listItemResId) {
			super(context, null, 0);
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mListItemResId = listItemResId;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			final boolean isConnected = cursor
					.getInt(cursor
							.getColumnIndex(LogContract.ConnectionEvent.COLUMN_NAME_CONNECTED)) == 1;
			final long logTime = cursor
					.getLong(cursor
							.getColumnIndex(LogContract.ConnectionEvent.COLUMN_NAME_TIME));
			final String logMessage = cursor
					.getString(cursor
							.getColumnIndex(LogContract.ConnectionEvent.COLUMN_NAME_MESSAGE));

			TextView textViewStatus = (TextView) view
					.findViewById(R.id.textViewStatus); 
			TextView textViewTime = (TextView) view
					.findViewById(R.id.textViewTime);

			

			textViewTime.setText(DateUtils.getRelativeDateTimeString(
					LogActivity.this, logTime, DateUtils.SECOND_IN_MILLIS,
					DateUtils.DAY_IN_MILLIS, 0));
			
			if (!TextUtils.isEmpty(logMessage)) {
				textViewStatus.setText(logMessage);
			} else {
				if (isConnected) {
					textViewStatus.setText(R.string.log_connected);
				} else {
					textViewStatus.setText(R.string.log_disconnected);
				}
			}
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return inflater.inflate(mListItemResId, parent, false);
		}

	}

}
