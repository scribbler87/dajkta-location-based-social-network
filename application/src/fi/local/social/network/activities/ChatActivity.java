package fi.local.social.network.activities;

import java.util.ArrayList;
import java.util.List;

import fi.local.social.network.R;
import fi.local.social.network.db.ChatMessage;
import fi.local.social.network.db.ChatMessageImpl;
import fi.local.social.network.db.ChatMessagesDataSource;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ChatActivity extends Activity {

	private EditText edittext;
	private List<ChatMessage> chatList;
	private BroadcastReceiver chatMessageReceiver;
	private ChatMessagesDataSource chatMessageDataSource;
	private ArrayAdapter<ChatMessage> adapter;
	private ListView chatHistoryListView;
	private String userName;
	private String receiverName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);

		Button sendButton = (Button) findViewById(R.id.buttonChatConfirm);
		edittext = (EditText) findViewById(R.id.edit_text_out);
		// ListView chatHist = (ListView) findViewById(R.id.listChat);
		chatList = new ArrayList<ChatMessage>();

		Bundle extras = getIntent().getExtras();
		userName = (String) extras.get("username");
		receiverName = extras.get("receiver").toString();

		chatMessageReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				String chatMessage = arg1.getAction();
				// ChatMessage cm = new ChatMessageImpl();
				Log.i("ChatActivity", chatMessage);
			}
		};

		registerReceiver(chatMessageReceiver, createChatMessageFilter());

		chatMessageDataSource = new ChatMessagesDataSource(this);
		chatMessageDataSource.open();

		filterMyMessages(); // we also receive others messages

		ArrayAdapter<ChatMessage> adapter = new ArrayAdapter<ChatMessage>(this,
				android.R.layout.simple_list_item_1, chatList);

		chatHistoryListView = (ListView) findViewById(R.id.listChat);
		chatHistoryListView.setAdapter(adapter);

		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Send a message using content of the edit text widget
				String message = edittext.getText().toString();
				sendMessage(message);
			}
		});

		edittext.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {

					String message = edittext.getText().toString();
					sendMessage(message);
					return true;
				}

				return false;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(chatMessageReceiver, createChatMessageFilter());
		Bundle extras = getIntent().getExtras();
		userName = (String) extras.get("username");
		receiverName = extras.get("receiver").toString();

		chatMessageDataSource.open();
		this.chatList = new ArrayList<ChatMessage>();
		filterMyMessages();
	}

	private static IntentFilter createChatMessageFilter() {
		return new IntentFilter("chatmessage");
	}

	private void filterMyMessages() {
		List<ChatMessage> allMessages = chatMessageDataSource.getAllEntries();
		for (ChatMessage chatMessage : allMessages) {
			if (chatMessage.getReceiverName().equals(receiverName)
					|| chatMessage.getSenderName().equals(receiverName))
				addChatMessage(chatMessage);
		}
	}

	private void addChatMessage(ChatMessage chatMessage) {
		chatList.add(chatMessage);
	}

	private void sendMessage(String message) {
		// Check that there's actually something to send
		if (message.length() > 0) {
			System.err.println(message);

			// TODO: get it from the network or somewhere else
			// generating string for storing in db

			ChatMessage tmpMessage = new ChatMessageImpl();
			tmpMessage.setMessage(message);
			tmpMessage.setReceiverName(receiverName);
			tmpMessage.setSenderName(userName);

			// Save the new comment to the database
			ChatMessage chatMessage = (ChatMessage) chatMessageDataSource
					.createEntry(tmpMessage.getDBString());

			addChatMessage(chatMessage);
			clearEditField();

			// send the message to the bluetooth
			Intent intent = new Intent();
			intent.putExtra("chatmessage", message);

			sendBroadcast(intent);
		}
	}

	private void clearEditField() {

		String string = "";
		edittext.setText(string);
	}

	@Override
	protected void onPause() {
		super.onPause();
		cleanUpResources();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cleanUpResources();
	}

	private void cleanUpResources() {
		unregisterReceiver(chatMessageReceiver);
		chatMessageDataSource.close();
	}

	public ArrayAdapter<ChatMessage> getAdapter() {
		return adapter;
	}

	public void setAdapter(ArrayAdapter<ChatMessage> adapter) {
		this.adapter = adapter;
	}

}