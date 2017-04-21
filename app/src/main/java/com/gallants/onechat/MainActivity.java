package com.gallants.onechat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

	public static final String PREF_NAME = "pref";
	public static final String KEY_AUTH = "isAuthenticated";
	public static final String KEY_NAME = "username";

	public static Client mClient;
	public static String username;

	EditText usernameText;
	EditText passwordText;
	Button loginButton;
	Button registerButton;

	public static ArrayList<String> onlineUsersList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		usernameText = (EditText) findViewById(R.id.usernameText);
		passwordText = (EditText) findViewById(R.id.passwordText);

		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String username = usernameText.getText().toString().trim();
				String password = passwordText.getText().toString().trim();
				try {
					new LoginTask().execute(username, password);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		registerButton = (Button) findViewById(R.id.registerButton);
		registerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String username = usernameText.getText().toString().trim();
				String password = passwordText.getText().toString().trim();
				try {
					new RegisterTask().execute(username, password);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		onlineUsersList = new ArrayList<>();

		new ConnectTask().execute("");
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (checkIfLoggedIn())
			goToMessageActivity();
	}

	public class ConnectTask extends AsyncTask<String, Void, Void> {

		boolean status = true;

		@Override
		protected Void doInBackground(String... params) {
			// create a Client object
			mClient = new Client();
			try {
				mClient.connect();
			} catch (ConnectException e) {
				status = false;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			if (!status)
				Toast.makeText(getApplicationContext(), "Couldn't connect to server!", Toast.LENGTH_SHORT).show();
		}
	}

	public class LoginTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			Log.i("AppInfo : ", params[0]);
			Log.i("AppInfo : ", params[1]);
			username = params[0];
			boolean status = false;
			try {
				status = mClient.login(params[0], params[1]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return status;
		}

		@Override
		protected void onPostExecute(Boolean status) {
			super.onPostExecute(status);

			Log.i("AppInfo : ", status.toString());

			if (status) {
				login();
			} else {
				Toast.makeText(getApplicationContext(), "Invalid username/pass", Toast.LENGTH_SHORT).show();
			}
		}


	}

	public class RegisterTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			boolean status = false;
			try {
				status = mClient.register(params[0], params[1]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return status;
		}

		@Override
		protected void onPostExecute(Boolean status) {
			super.onPostExecute(status);

			if (status) {
				goToMessageActivity();
			} else {
				Toast.makeText(getApplicationContext(), "Username already taken! Try again", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void goToMessageActivity() {
		Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
		startActivity(intent);
	}

	/**
	 * populate onlineUsersList array
	 * @param users String of the form "['abcd', 'name2', ...]"
	 */
	public static void populateOnlineUsers(String users) {
		String[] parts = users.split("'");
		for (int i = 1; i < parts.length; i += 2) {
			onlineUsersList.add(parts[i]);
		}
	}

	public boolean checkIfLoggedIn() {
		SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		return pref.contains(KEY_AUTH) && pref.getBoolean(KEY_AUTH, false);
	}

	public void login() {
		SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(KEY_AUTH, true);
		editor.putString(KEY_NAME, username);
		editor.apply();

		goToMessageActivity();
	}
}
