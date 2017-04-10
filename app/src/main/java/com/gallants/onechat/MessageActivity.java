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

		new ListenTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mClient.stopListening();
		Log.i("AppInfo", "stopping..");
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
//				Toast.makeText(getApplicationContext(), values[0], Toast.LENGTH_SHORT).show();
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
