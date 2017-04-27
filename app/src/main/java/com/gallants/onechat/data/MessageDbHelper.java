package com.gallants.onechat.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gallants.onechat.data.MessageContract.MessageEntry;

/**
 * Created by vinayakvivek on 4/27/17.
 */

public class MessageDbHelper extends SQLiteOpenHelper {

	// If you change the database schema, you must increment the database version.
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "user.db";

	public MessageDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		final String SQL_CREATE_MESSAGES_TABLE = "CREATE TABLE " + MessageEntry.TABLE_NAME + " (" +
				MessageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				MessageEntry.COLUMN_SOURCE + " TEXT UNIQUE NOT NULL, " +
				MessageEntry.COLUMN_MESSAGE + " TEXT NOT NULL, " +
				MessageEntry.COLUMN_TIME + " TIMESTAMP DEFAULT (datetime('now','localtime')) );";

		db.execSQL(SQL_CREATE_MESSAGES_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + MessageEntry.TABLE_NAME);
		onCreate(db);
	}
}
