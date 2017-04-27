package com.gallants.onechat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class PeerMessageActivity extends AppCompatActivity {

	private Client mClient;

	EditText messageText;
	Button sendButton;

	public static ArrayList<String> messageList;
	public static ArrayAdapter<String> messageAdapter;

	ListView messageListView;

	// loggedInUser whose messages are open
	String name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_peer_message);

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

		Bundle bundle = getIntent().getExtras();
		name = bundle.getString("name");
		setTitle("Messages from " + name);

		if (UsersListActivity.usersList.contains(name)) {
			messageList = Utility.getMessages(name, MainActivity.loggedInUser, getApplicationContext());
		} else {
			messageList = new ArrayList<>();
			UsersListActivity.usersListAdapter.add(name);
		}
		messageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, messageList);

		messageListView = (ListView) findViewById(R.id.messageListView);
		messageListView.setAdapter(messageAdapter);
	}

	public static void updateMessageList(String message) {
		messageAdapter.add(message);
	}

	public class SendTask extends AsyncTask<String, Void, Void> {

		boolean status = true;
		String message = "";

		@Override
		protected Void doInBackground(String... strings) {
			try {
				mClient.sendMessage(name + "@" + strings[0]);
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
				Utility.saveMessage(name, MainActivity.loggedInUser, "[Me] : " + message, getApplicationContext());
			} else {
				toastText = "Could not send message!";
			}

			Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
		}
	}
}
