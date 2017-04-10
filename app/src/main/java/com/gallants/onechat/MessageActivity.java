package com.gallants.onechat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {

	private Client mClient;

	EditText messageText;
	Button sendButton;

	ListView messageListView;
	ArrayList<String> messageList;
	ArrayAdapter<String> messageAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);

		mClient = MainActivity.mClient;

		messageText = (EditText) findViewById(R.id.messageText);
		sendButton = (Button) findViewById(R.id.sendButton);
		sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String message = messageText.getText().toString().trim();
				new SendTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, message);
				Log.i("AppInfo : ", "Sending message");
			}
		});

		messageListView = (ListView) findViewById(R.id.messageListView);
		messageList = new ArrayList<>();
		messageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, messageList);

		messageListView.setAdapter(messageAdapter);
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
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		new ListenTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	protected void onPause() {
		super.onPause();
//		mClient.stopListening();
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
				Log.i("AppInfo : ", " message : " + values[0]);

				String[] parts = values[0].split("\\s+");

				if (parts.length > 1) {
					if (parts[0].compareTo("[join]") == 0) {
						MainActivity.onlineUsersList.add(parts[1]);
					} else if (parts[0].compareTo("[offline]") == 0) {
						MainActivity.onlineUsersList.remove(parts[1]);
					}
				}

				messageAdapter.add(values[0]);
			}
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			Log.i("AppInfo", "stopped listening");
		}
	}

	public class SendTask extends AsyncTask<String, Void, Void> {

		boolean status = true;
		String message = "";

		@Override
		protected Void doInBackground(String... strings) {
			try {
				mClient.sendMessage(strings[0]);
				message = strings[0];
			} catch (IOException e) {
				status = false;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);

			String toastText = "";
			if (status) {
				toastText = "Message send successfully!";
				messageAdapter.add("[Me] : " + message);
			} else {
				toastText = "Could not send message!";
			}

			Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
		}
	}
}
