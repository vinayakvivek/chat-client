package com.gallants.onechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AddUserActivity extends AppCompatActivity {

	TextInputEditText newUserText;
	Button addNewUserButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_user);

		newUserText = (TextInputEditText) findViewById(R.id.newUserText);
		addNewUserButton = (Button) findViewById(R.id.addNewUserButton);

		addNewUserButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String username = newUserText.getText().toString().trim();
				if (username.isEmpty()) {
					Toast.makeText(getApplicationContext(), "Enter a username", Toast.LENGTH_SHORT).show();
				} else if (UsersListActivity.usersList.contains(username)) {
					Toast.makeText(getApplicationContext(), "User already exists in contacts", Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent(getApplicationContext(), PeerMessageActivity.class);
					intent.putExtra("name", username);
					startActivity(intent);
				}
			}
		});
	}
}
