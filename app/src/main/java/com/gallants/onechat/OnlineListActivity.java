package com.gallants.onechat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class OnlineListActivity extends AppCompatActivity {

	ArrayAdapter<String> onlineUserAdapter;
	ListView onlineUsersListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_online_list);

		onlineUsersListView = (ListView) findViewById(R.id.onlineUsersListView);
		onlineUserAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, MainActivity.onlineUsersList);
		onlineUsersListView.setAdapter(onlineUserAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		onlineUserAdapter.notifyDataSetChanged();

		Log.i("AppInfo", MainActivity.onlineUsersList.toString());
	}
}
