package com.gallants.onechat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static com.gallants.onechat.MainActivity.KEY_AUTH;
import static com.gallants.onechat.MainActivity.KEY_NAME;
import static com.gallants.onechat.MainActivity.PREF_NAME;

public class UsersListActivity extends AppCompatActivity {

	private Client mClient;

	ArrayList<String> usersList;
	ArrayAdapter<String> usersListAdapter;

	ListView usersListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_users_list);

		mClient = MainActivity.mClient;

		usersListView = (ListView) findViewById(R.id.usersListView);

		usersList = Utility.getUsers(this);
		usersListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, usersList);
		usersListView.setAdapter(usersListAdapter);

		new ListenTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mClient.stopListening();

		SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(KEY_AUTH, false);
		editor.putString(KEY_NAME, "");
		editor.apply();
	}

	public class ListenTask extends AsyncTask<Void, String, Void> {

		@Override
		protected Void doInBackground(Void... voids) {
			mClient.setMessageListener(new Client.OnMessageReceived() {
				@Override
				public void messageReceived(String message) {
					publishProgress(message);
				}
			});
			mClient.startListening();
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);

			if (!values[0].isEmpty()) {

				String[] parts = values[0].split("\\s+");

				if (parts.length > 1) {
					if (parts[0].compareTo("[join]") == 0) {
						MainActivity.onlineUsersList.add(parts[1]);
					} else if (parts[0].compareTo("[offline]") == 0) {
						MainActivity.onlineUsersList.remove(parts[1]);
					} else if (parts[0].compareTo("message") == 0) {
						String username = parts[1];
						String message = values[0].substring(7 + username.length() + 2);
						Log.i("AppInfo", "[message] : " + message);
						Utility.saveMessage(username, message, getApplicationContext());

						if (!usersList.contains(username)) {
							usersListAdapter.add(username);
						}
					}
				}
			}
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			Log.i("AppInfo", "stopped listening");
		}
	}
}
