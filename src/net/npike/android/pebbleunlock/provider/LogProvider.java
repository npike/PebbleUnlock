package net.npike.android.pebbleunlock.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Set;

import net.npike.android.pebbleunlock.BuildConfig;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

/**
 * Provides access to a database of MaaS segments.
 */
public class LogProvider extends ContentProvider {

	private volatile boolean mNotificationsSuspended;
	private LinkedList<Uri> mSuspendedNotifications = new LinkedList<Uri>();
	private Set<Uri> mSuspendedNotificationsSet = new HashSet<Uri>();

	static class ProviderDatabaseHelper extends SQLiteOpenHelper {
		/**
		 * The database that the provider uses as its underlying data store
		 */
		private static final String DATABASE_NAME = "log.db";
		/**
		 * The database version
		 */
		private static final int DATABASE_VERSION = 1;
		@SuppressWarnings("unused")
		private Context mContext;

		/**
		 * Creates a new DatabaseHelper
		 * 
		 * @param context
		 *            context of this database
		 */
		ProviderDatabaseHelper(final Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			mContext = context;
		}

		/**
		 * Creates the underlying database with table name and column names
		 * taken from the ColorContract class.
		 */
		@Override
		public void onCreate(final SQLiteDatabase db) {
			if (BuildConfig.DEBUG) {
				Log.d(LogProvider.class.getSimpleName(), "Creating the "
						+ LogContract.ConnectionEvent.TABLE_NAME + " table");
			}

			db.execSQL("CREATE TABLE " + LogContract.ConnectionEvent.TABLE_NAME
					+ " (" + BaseColumns._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ LogContract.ConnectionEvent.COLUMN_NAME_CONNECTED + " INTEGER default 0, " 
					+ LogContract.ConnectionEvent.COLUMN_NAME_MESSAGE + " TEXT, " 
					+ LogContract.ConnectionEvent.COLUMN_NAME_TIME + " INTEGER " + ");");

		}

		/**
		 * 
		 * Demonstrates that the provider must consider what happens when the
		 * underlying database is changed.
		 */
		@Override
		public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
				final int newVersion) {
			Log.w(LogProvider.class.getSimpleName(),
					"Upgrading database from version " + oldVersion + " to "
							+ newVersion + ", which will destroy all old data");
			// db.execSQL("DROP TABLE IF EXISTS " +
			// LogContract.ConnectionEvent.TABLE_NAME);

			this.onCreate(db);
		}
	}

	/**
	 * A UriMatcher instance
	 */
	private static final UriMatcher uriMatcher = LogProvider.buildUriMatcher();

	private static final int CONNETION_EVENT_ID = 2;
	private static final int CONNECTION_EVENT = 1;

	/**
	 * Creates and initializes the URI matcher
	 * 
	 * @return the URI Matcher
	 */
	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

		matcher.addURI(LogContract.AUTHORITY, "connection_events",
				LogProvider.CONNECTION_EVENT);

		matcher.addURI(LogContract.AUTHORITY, "connection_events/#",
				LogProvider.CONNETION_EVENT_ID);

		return matcher;
	}

	/**
	 * Handle to a new DatabaseHelper.
	 */
	private ProviderDatabaseHelper databaseHelper;

	@Override
	public int delete(final Uri uri, final String where,
			final String[] whereArgs) {
		// Opens the database object in "write" mode.
		final SQLiteDatabase db = databaseHelper.getWritableDatabase();
		int count;
		// Does the delete based on the incoming URI pattern.
		switch (LogProvider.uriMatcher.match(uri)) {
		case CONNECTION_EVENT:
			// If the incoming pattern matches the general pattern for makes,
			// does a delete based on the
			// incoming "where" columns and arguments.
			count = db.delete(LogContract.ConnectionEvent.TABLE_NAME, where,
					whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(final Uri uri) {
		/**
		 * Chooses the MIME type based on the incoming URI pattern
		 */
		switch (LogProvider.uriMatcher.match(uri)) {
		case CONNECTION_EVENT:
			// If the pattern is for makess, returns the general content type.
			return LogContract.ConnectionEvent.CONTENT_TYPE;
		case CONNETION_EVENT_ID:
			// If the pattern is for make IDs, returns the make ID content
			// type.
			return LogContract.ConnectionEvent.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(final Uri uri, final ContentValues initialValues) {
		switch (LogProvider.uriMatcher.match(uri)) {
		case CONNECTION_EVENT:
			ContentValues values;
			if (initialValues != null) {
				values = new ContentValues(initialValues);
			} else {
				values = new ContentValues();
			}

			final SQLiteDatabase db = databaseHelper.getWritableDatabase();

			final long rowId = db.insert(
					LogContract.ConnectionEvent.TABLE_NAME,
					LogContract.ConnectionEvent.COLUMN_NAME_CONNECTED, values);
			// If the insert succeeded, the row ID exists.
			if (rowId > 0) {
				// Creates a URI with the make ID pattern and the new row ID
				// appended to it.
				final Uri inserUri = ContentUris.withAppendedId(
						LogContract.ConnectionEvent.CONTENT_ID_URI_BASE, rowId);

				notifyChange(uri);

				return inserUri;
			}
			return uri;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	/**
	 * Creates the underlying DatabaseHelper
	 * 
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		databaseHelper = new ProviderDatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(final Uri uri, final String[] projection,
			final String selection, final String[] selectionArgs,
			final String sortOrder) {

		switch (LogProvider.uriMatcher.match(uri)) {
		case CONNECTION_EVENT:
		case CONNETION_EVENT_ID:
			// Constructs a new query builder and sets its table name
			final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(LogContract.ConnectionEvent.TABLE_NAME);
			final HashMap<String, String> allColumnProjectionMap = new HashMap<String, String>();
			allColumnProjectionMap.put(BaseColumns._ID, BaseColumns._ID);
			allColumnProjectionMap.put(
					LogContract.ConnectionEvent.COLUMN_NAME_CONNECTED,
					LogContract.ConnectionEvent.COLUMN_NAME_CONNECTED);
			allColumnProjectionMap.put(
					LogContract.ConnectionEvent.COLUMN_NAME_TIME,
					LogContract.ConnectionEvent.COLUMN_NAME_TIME);
			allColumnProjectionMap.put(
					LogContract.ConnectionEvent.COLUMN_NAME_MESSAGE,
					LogContract.ConnectionEvent.COLUMN_NAME_MESSAGE);
			

			qb.setProjectionMap(allColumnProjectionMap);
			switch (LogProvider.uriMatcher.match(uri)) {
			case CONNECTION_EVENT:
				break;
			case CONNETION_EVENT_ID:
				// If the incoming URI is for a single make identified by its ID,
				// appends "_ID = <makeID>"
				// to the where clause, so that it selects that single make
				qb.appendWhere(BaseColumns._ID
						+ "="
						+ uri.getPathSegments().get(
								LogContract.ConnectionEvent.ID_PATH_POSITION));
				break;
			}
			String orderBy;
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = LogContract.ConnectionEvent.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			final SQLiteDatabase db = databaseHelper.getReadableDatabase();
			final Cursor c = qb.query(db, projection, selection, selectionArgs,
					null, null, orderBy);
			c.setNotificationUri(getContext().getContentResolver(), uri);
			return c;
		default:
			return null;
		}

	}
 
	@Override
	public int update(final Uri uri, final ContentValues values,
			final String selection, final String[] selectionArgs) {

		return 0;
	}

	 

	@Override
	public ContentProviderResult[] applyBatch(
			ArrayList<ContentProviderOperation> operations)
			throws OperationApplicationException {
		setNotificationsSuspended(true);
		databaseHelper.getWritableDatabase().beginTransaction();
		ContentProviderResult[] result = null;
		try {
			result = super.applyBatch(operations);
			databaseHelper.getWritableDatabase().setTransactionSuccessful();
		} finally {
			databaseHelper.getWritableDatabase().endTransaction();
		}
		setNotificationsSuspended(false);
		return result;
	}

	protected void setNotificationsSuspended(boolean suspended) {
		mNotificationsSuspended = suspended;
		if (!suspended) {
			notifyOutstandingChanges();
		}
	}

	protected void notifyOutstandingChanges() {
		Uri uri;
		while ((uri = mSuspendedNotifications.poll()) != null) {
			getContext().getContentResolver().notifyChange(uri, null);
			mSuspendedNotificationsSet.remove(uri);
		}
	}

	protected void notifyChange(Uri uri) {
		if (mNotificationsSuspended) {
			synchronized (mSuspendedNotificationsSet) {
				if (mSuspendedNotificationsSet.contains(uri)) {
					mSuspendedNotifications.remove(uri);
				}
				mSuspendedNotifications.add(uri);
				mSuspendedNotificationsSet.add(uri);
			}
		} else {
			getContext().getContentResolver().notifyChange(uri, null);
		}
	}

	public static String getGeneratedFriendlyName(String notFriendlyName) {
		return notFriendlyName.replaceAll("\\W", "").toLowerCase(Locale.US);
	}

}
