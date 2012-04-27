package fi.local.social.network.bttest;

import android.content.Intent;

public class BTIntent {
	public static final Intent CHAT_MESSAGE = new Intent("chat_message");
	public static final Intent CHAT_CLEAR = new Intent("chat_clear");
	static final String CHAT_MESSAGE_PATH = "fi.local.social.network.bttest.chat_message";
	public static final Intent EVENT_ADD = new Intent("event_add");
	public static final String EVENT_ADD_PATH = "fi.local.social.network.bttest.event_message";
	public static final Intent EVENT_RECEIVE = new Intent("event_receive");
	static final String EVENT_RECEIVE_PATH = "fi.local.social.network.bttest.event_receive";
	
}
