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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

	private static Context context;

	public static final String PREF_NAME = "pref";
	public static final String KEY_AUTH = "isAuthenticated";
	public static final String KEY_NAME = "loggedInUser";

	public static Client mClient;
	public static String loggedInUser;

	EditText usernameText;
	EditText passwordText;
	Button loginButton;
	Button registerButton;
	Button ldapLoginButton;

	public static ArrayList<String> onlineUsersList;
	public static ArrayList<String> usersWithNewMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = getApplicationContext();

		usernameText = (EditText) findViewById(R.id.usernameText);
		passwordText = (EditText) findViewById(R.id.passwordText);

		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startTask("\\login");
			}
		});

		registerButton = (Button) findViewById(R.id.registerButton);
		registerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startTask("\\register");
			}
		});

		ldapLoginButton = (Button) findViewById(R.id.ldapLoginButton);
		ldapLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startTask("\\ldaplogin");
			}
		});

		onlineUsersList = new ArrayList<>();
		usersWithNewMessage = new ArrayList<>();
	}

	public void startTask(String cmd) {
		String username = usernameText.getText().toString().trim();
		String password = passwordText.getText().toString().trim();

		if (username.isEmpty()) {
			Toast.makeText(getApplicationContext(), "Enter username", Toast.LENGTH_SHORT).show();
		} else if (password.isEmpty()) {
			Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
		} else {
			try {
				new Task().execute(username, password, cmd);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (checkIfLoggedIn())
			goToMessageActivity();
		else
			new ConnectTask().execute("");
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

	public class Task extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			Log.i("AppInfo : ", params[0]);
			Log.i("AppInfo : ", params[1]);
			loggedInUser = params[0];
			String status = "";
			try {
				status = mClient.command(params[0], params[1], params[2]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return status;
		}

		@Override
		protected void onPostExecute(String status) {
			super.onPostExecute(status);

			if (status.compareTo("1") == 0) {
				login();
			} else {
				Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
			}
		}
	}


	public void goToMessageActivity() {
		Intent intent = new Intent(getApplicationContext(), UsersListActivity.class);
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

	public static void savePendingMessages(String pendingMessages) {
		try {
			JSONObject messages = new JSONObject(pendingMessages);
			JSONArray msgArray = messages.getJSONArray("messages");
			Log.i("AppInfo", msgArray.toString());
			for (int i = 0; i < msgArray.length(); ++i) {
				JSONObject msg = msgArray.getJSONObject(i);
				usersWithNewMessage.add(msg.getString("source"));
				Utility.saveMessage(msg.getString("source"), loggedInUser, msg.getString("message"), context);
			}
		} catch (JSONException e) {
			e.printStackTrace();
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
		editor.putString(KEY_NAME, loggedInUser);
		editor.apply();

		goToMessageActivity();
	}
}
