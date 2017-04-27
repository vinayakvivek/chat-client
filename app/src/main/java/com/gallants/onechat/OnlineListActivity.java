package com.gallants.onechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class OnlineListActivity extends AppCompatActivity {

	ArrayAdapter<String> onlineUserAdapter;
	ListView onlineUsersListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_online_list);

		setTitle("Online Users");

		onlineUsersListView = (ListView) findViewById(R.id.onlineUsersListView);
		onlineUserAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, MainActivity.onlineUsersList);
		onlineUsersListView.setAdapter(onlineUserAdapter);

		onlineUsersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				Log.i("AppInfo", "user no - " + i);
				Intent intent = new Intent(getApplicationContext(), PeerMessageActivity.class);
				Object ob = onlineUsersListView.getItemAtPosition(i);
				String str = (String)ob ;
				Toast.makeText(getApplicationContext(),str,Toast.LENGTH_SHORT).show();
				Bundle bundle = new Bundle();
				bundle.putString("name", str);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		onlineUserAdapter.notifyDataSetChanged();
		Log.i("AppInfo", MainActivity.onlineUsersList.toString());
	}
}
