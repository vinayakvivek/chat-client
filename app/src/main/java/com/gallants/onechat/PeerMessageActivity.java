package com.gallants.onechat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class PeerMessageActivity extends AppCompatActivity {

	ArrayList<String> messageList;
	ArrayAdapter<String> messageAdapter;

	ListView messageListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_peer_message);

		Bundle bundle = getIntent().getExtras();
		String name = bundle.getString("name");

		ArrayList<String> messages = Utility.getMessages(name, getApplicationContext());

		for (String m : messages) {
			Log.i("AppInfo", name + " @ " + m);
		}
	}
}
