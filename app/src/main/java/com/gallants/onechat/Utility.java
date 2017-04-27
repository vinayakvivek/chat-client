package com.gallants.onechat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.gallants.onechat.data.MessageContract.MessageEntry;

import java.util.ArrayList;

/**
 * Created by vinayakvivek on 4/27/17.
 */

public class Utility {

	public static boolean saveMessage(String source, String message, Context context) {
		ContentValues values = new ContentValues();
		values.put(MessageEntry.COLUMN_SOURCE, source);
		values.put(MessageEntry.COLUMN_MESSAGE, message);

		Log.i("AppInfo", "saving message");

		try {
			context.getContentResolver().insert(
					MessageEntry.CONTENT_URI,
					values
			);

			Log.i("AppInfo", "saved message");
			return true;
		} catch (Exception e) {
			Log.i("AppInfo", "save error : " + e.toString());
			return false;
		}
	}

	public static ArrayList<String> getUsers(Context context) {
		ArrayList<String> users = new ArrayList<>();

		Cursor messageCursor = context.getContentResolver().query(
				MessageEntry.CONTENT_URI,
				new String[]{"DISTINCT " + MessageEntry.COLUMN_SOURCE},
				null,
				null,
				null
		);

		if (messageCursor != null && messageCursor.moveToFirst()) {
			while (!messageCursor.isAfterLast()) {
				String username = messageCursor.getString(messageCursor.getColumnIndex(MessageEntry.COLUMN_SOURCE));
				users.add(username);
				messageCursor.moveToNext();
			}
		}

		return users;
	}

	public static ArrayList<String> getMessages(String username, Context context) {
		ArrayList<String> messageList = new ArrayList<>();

		Cursor messageCursor = context.getContentResolver().query(
				MessageEntry.CONTENT_URI,
				new String[]{MessageEntry.COLUMN_MESSAGE},
				MessageEntry.COLUMN_SOURCE + "= ?",
				new String[]{username},
				null
		);

		if (messageCursor != null && messageCursor.moveToFirst()) {
			while (!messageCursor.isAfterLast()) {
				String message = messageCursor.getString(messageCursor.getColumnIndex(MessageEntry.COLUMN_MESSAGE));
				messageList.add(message);
				messageCursor.moveToNext();
			}
		}

		return messageList;
	}
}
