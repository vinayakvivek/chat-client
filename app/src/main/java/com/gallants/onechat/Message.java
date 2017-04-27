package com.gallants.onechat;

/**
 * Created by vinayakvivek on 4/27/17.
 */

public class Message {
	String source;
	String message;

	public Message() {
		source = "";
		message = "";
	}

	public Message(String source, String message) {
		this.source = source;
		this.message = message;
	}

	public String getSource() {
		return source;
	}

	public String getMessage() {
		return message;
	}
}
