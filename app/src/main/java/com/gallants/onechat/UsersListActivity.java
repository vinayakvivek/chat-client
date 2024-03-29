package com.gallants.onechat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static com.gallants.onechat.MainActivity.KEY_AUTH;
import static com.gallants.onechat.MainActivity.KEY_NAME;
import static com.gallants.onechat.MainActivity.PREF_NAME;

public class UsersListActivity extends AppCompatActivity {

	private Client mClient;

	public static ArrayList<String> usersList;
	public static ArrayAdapter<String> usersListAdapter;

	ListView usersListView;
	FloatingActionButton addNewUserFAB;

	String currentUser = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_users_list);

		setTitle("Contacts");

		mClient = MainActivity.mClient;

		usersListView = (ListView) findViewById(R.id.usersListView);

		usersList = Utility.getUsers(MainActivity.loggedInUser, this);
		usersListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, usersList);
		usersListView.setAdapter(usersListAdapter);

		usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				currentUser = usersList.get(i);
				Intent intent = new Intent(getApplicationContext(), PeerMessageActivity.class);
				intent.putExtra("name", currentUser);
				startActivity(intent);
			}
		});

		addNewUserFAB = (FloatingActionButton) findViewById(R.id.addNewUserFAB);
		addNewUserFAB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(getApplicationContext(), AddUserActivity.class));
			}
		});

		new ListenTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public void updateListWhenMessageComes() {
		for (String user : MainActivity.usersWithNewMessage) {
			if (usersList.contains(user)) {
				int pos = usersList.indexOf(user);
				Log.i("AppInfo", "uset at " + pos);

				View v = usersListView.getAdapter().getView(pos, null, usersListView);
				Log.i("AppInfo", v.toString());
				v.animate().rotation(10).start();

//				usersListView.getChildAt(usersList.indexOf(user)).setBackgroundColor(
//						getResources().getColor(android.R.color.holo_orange_light));
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
//		updateListWhenMessageComes();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		logout();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(startMain);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_message, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.action_online_users :
				startActivity(new Intent(this, OnlineListActivity.class));
				return true;
			case R.id.action_logout :
				logout();
				startActivity(new Intent(this, MainActivity.class));
				return true;
		}

		return super.onOptionsItemSelected(item);
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
						Utility.saveMessage(username, MainActivity.loggedInUser, message, getApplicationContext());

						if (!usersList.contains(username)) {
							usersListAdapter.add(username);
						}

						if (username.compareTo(currentUser) == 0) {
							PeerMessageActivity.updateMessageList(message);
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

	public void logout() {
		mClient.stopListening();

		SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(KEY_AUTH, false);
		editor.putString(KEY_NAME, "");
		editor.apply();
	}
}
