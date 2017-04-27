package com.gallants.onechat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PeerMessageActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_peer_message);

		Bundle bundle = getIntent().getExtras();
		String name = bundle.getString("name");
	}
}
