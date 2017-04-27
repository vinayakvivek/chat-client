package com.gallants.onechat.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gallants.onechat.data.MessageContract.MessageEntry;

/**
 * Created by vinayakvivek on 4/27/17.
 */

public class MessageProvider extends ContentProvider {

	// The URI Matcher used by this content provider.
	private static final UriMatcher sUriMatcher = buildUriMatcher();
	private MessageDbHelper mOpenHelper;

	static final int MESSAGE = 100;

	static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = MessageContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, MessageContract.PATH_MESSAGE, MESSAGE);

		return matcher;
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new MessageDbHelper(getContext());
		return false;
	}

	@Nullable
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Cursor retCursor;
		switch (sUriMatcher.match(uri)) {
			case MESSAGE: {
				retCursor = mOpenHelper.getReadableDatabase().query(MessageEntry.TABLE_NAME,
						projection,
						selection,
						selectionArgs,
						null,
						null,
						sortOrder);
				break;
			}
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		return retCursor;
	}

	@Nullable
	@Override
	public String getType(@NonNull Uri uri) {
		return null;
	}

	@Nullable
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		Uri returnUri;

		switch (match) {
			case MESSAGE: {
				long _id = db.insert(MessageEntry.TABLE_NAME, null, values);
				if (_id > 0) {
					returnUri = MessageEntry.buildUserUri(_id);
				} else {
					throw new android.database.SQLException("Failed to insert row into " + uri);
				}
				break;
			}
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		if (getContext() != null)
			getContext().getContentResolver().notifyChange(uri, null);

		return returnUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		int rowsDeleted;

		// all rows are deleted
		if ( null == selection ) selection = "1";

		switch (match) {
			case MESSAGE: {
				rowsDeleted = db.delete(
						MessageEntry.TABLE_NAME, selection, selectionArgs);
				break;
			}
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		// Because a null deletes all rows
		if (rowsDeleted != 0 && getContext() != null) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		int rowsUpdated;

		switch (match) {
			case MESSAGE: {
				rowsUpdated = db.update(MessageEntry.TABLE_NAME, values, selection,
						selectionArgs);
				break;
			}
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		if (rowsUpdated != 0 && getContext() != null) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return rowsUpdated;
	}
}
