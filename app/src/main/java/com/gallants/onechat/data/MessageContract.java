package com.gallants.onechat.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by vinayakvivek on 4/27/17.
 */

public class MessageContract {

	public static final String CONTENT_AUTHORITY = "com.gallants.onechat";

	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	public static final String PATH_MESSAGE = "message";

	public static final class MessageEntry implements BaseColumns {
		public static final Uri CONTENT_URI =
				BASE_CONTENT_URI.buildUpon().appendPath(PATH_MESSAGE).build();

		public static final String CONTENT_TYPE =
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MESSAGE;
		public static final String CONTENT_ITEM_TYPE =
				ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MESSAGE;

		// Table name
		public static final String TABLE_NAME = "messages";

		public static final String COLUMN_SOURCE = "source";
		public static final String COLUMN_MESSAGE = "message";
		public static final String COLUMN_TIME = "time";

		public static Uri buildUserUri(long id) {
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}
	}
}
