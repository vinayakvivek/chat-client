package com.gallants.onechat;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;


public class Client {

//	private static final String SERVER_IP = "localhost";
	private static final String SERVER_IP = "10.0.2.2";
	private static final int SERVER_PORT = 9009;
	private OnMessageReceived mMessageListener = null;
	private static AtomicBoolean mRun = new AtomicBoolean(false);
	private Socket clientSocket;

	DataOutputStream out;
	BufferedReader in;

	/**
	 * Constructor of the class.
	 */
	public Client() {}

	public void setMessageListener(OnMessageReceived listener) {
		mMessageListener = listener;
	}

	/**
	 * Sends the message entered by client to the server
	 * @param message text entered by client
	 */
	public void sendMessage(String message) throws IOException {
		if (out != null) {
			out.writeBytes(message);
			out.flush();
		} else {
			Log.i("AppInfo", "writer is null");
		}
	}

	public boolean login(String username, String password) throws IOException {
		String cmd = "\\login";

		sendMessage(cmd);
		sendMessage(username + "," + password);

		String status = in.readLine();
		Log.i("AppInfo : status ", status);

		sendMessage("1");

		String onlineUsers = in.readLine();
		Log.i("AppInfo", onlineUsers);

		MainActivity.populateOnlineUsers(onlineUsers);

		String pendingMessages = in.readLine();
		MainActivity.savePendingMessages(pendingMessages);

		return status.compareTo("1") == 0;
	}

	public boolean register(String username, String password) throws IOException {
		String cmd = "\\register";

		sendMessage(cmd);
		sendMessage(username + "," + password);

		String status = in.readLine();
		Log.i("AppInfo : status ", status);

		sendMessage("1");

		String onlineUsers = in.readLine();
		Log.i("AppInfo", onlineUsers);

		MainActivity.populateOnlineUsers(onlineUsers);

		String pendingMessages = in.readLine();
		MainActivity.savePendingMessages(pendingMessages);

		return status.compareTo("1") == 0;
	}

	public void stopListening() {
		mRun.set(false);
		try {
			clientSocket.close();
			Log.i("AppInfo", "closing socket");
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		won't work now since in.readLine() is thread blocking.. so startListening() will
		exit only if a message is received after setting mRun to false
		 */
	}

	public void startListening() {
		mRun.set(true);

		Log.i("AppInfo", "start listening..");

		while (mRun.get()) {
			try {
				String message = in.readLine();
				if (message != null && mMessageListener != null) {
					mMessageListener.messageReceived(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Log.i("AppInfo", "stopped listening");
	}

	public void connect() throws ConnectException {
		try {
			InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
			Log.e("serverAddress", serverAddr.toString());
			Log.e("TCP Client", "C: Connecting...");

			// create a socket to make the connection with the server
			clientSocket = new Socket(serverAddr, SERVER_PORT);
			Log.e("TCP Server IP", SERVER_IP);

			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new DataOutputStream(clientSocket.getOutputStream());

		} catch (Exception e) {
			Log.i("AppInfo :", e.toString());
			throw new ConnectException("Cannot connect to server!");
		}
	}

	public String getSERVERIP() {
		return SERVER_IP;
	}

	// Declare the interface. The method messageReceived(String message) will
	// must be implemented in the MyActivity
	// class at on asynckTask doInBackground
	public interface OnMessageReceived {
		public void messageReceived(String message);
	}
}
