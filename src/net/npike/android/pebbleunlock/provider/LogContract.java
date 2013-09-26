package net.npike.android.pebbleunlock.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class LogContract {
	public static final class ConnectionEvent implements BaseColumns {

		public static final String COLUMN_NAME_CONNECTED = "connected";
		public static final String COLUMN_NAME_MESSAGE = "message";
		public static final String COLUMN_NAME_TIME = "time"; 

		/**
		 * The content URI base for a single make. Callers must append a numeric
		 * segment id to this Uri to retrieve a make
		 */
		public static final Uri CONTENT_ID_URI_BASE = Uri.parse(LogContract.SCHEME + LogContract.AUTHORITY
				+ "/" + ConnectionEvent.TABLE_NAME + "/");
		/**
		 * The MIME type of a {@link #CONTENT_URI} single make.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.npike.pebbleunlock.connectionevent";

		/**
		 * The MIME type of a {@link #CONTENT_URI} a list of make.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.npike.pebbleunlock.connectionevent";

		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse(LogContract.SCHEME + LogContract.AUTHORITY + "/"
				+ ConnectionEvent.TABLE_NAME);
		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = ConnectionEvent.COLUMN_NAME_TIME + " ASC";
		/**
		 * The table name offered by this provider
		 */
		public static final String TABLE_NAME = "connection_events";
		/**
		 * 0-relative position of a make ID segment in the path part of a make
		 * ID URI
		 */
		public static final int ID_PATH_POSITION = 1;

		/**
		 * This class cannot be instantiated
		 */
		private ConnectionEvent() {
		}
	}

	/**
	 * Base authority for this content provider
	 */
	public static final String AUTHORITY = "net.npike.android.pebbleunlock";
	/**
	 * The scheme part for this provider's URI
	 */
	private static final String SCHEME = "content://";

	/**
	 * This class cannot be instantiated
	 */
	private LogContract() {
	}
}
