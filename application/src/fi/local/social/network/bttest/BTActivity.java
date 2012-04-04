package fi.local.social.network.bttest;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import fi.local.social.network.R;

public class BTActivity extends Activity {

	static final String PHASE_CONTENT_SEPARATOR = ":";
	static final String ID_SEPARATOR = "=";
	static final String KEY_VALUE_SEPARATOR = "#";
	static final String MESSAGE_SEPARATOR = "£";

	// Used by BlueToothChatService and BTActivity handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	private static final String EXTRA_DEVICE_ADDRESS = null;

	private BTEventSet eventSet = new BTEventSet();
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothChatService mChatService;

	private ArrayAdapter<String> mConversationArrayAdapter;
	private final BroadcastReceiver chatMessageBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.equals(BTIntent.CHAT_MESSAGE)) {
				mConversationArrayAdapter.add(intent
						.getStringExtra(BTIntent.CHAT_MESSAGE_PATH));
			} else if (intent.equals(BTIntent.CHAT_CLEAR)) {
				mConversationArrayAdapter.clear();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.btview);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		IntentFilter filter = new IntentFilter(
				BTIntent.CHAT_MESSAGE.getAction());
		filter.addAction(BTIntent.CHAT_MESSAGE.getAction());
		filter.addAction(BTIntent.CHAT_CLEAR.getAction());
		registerReceiver(chatMessageBroadcastReceiver, filter);

		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else {
			if (mChatService == null)
				setupChat();
		}
	}

	private void receivedIds(BTIdSet ids, BTProtocolPhase phase) {
		if (!ids.isEmpty()) {
			if (phase.equals(BTProtocolPhase.ADVERTISE)) {
				sendIds(eventSet.buildRequestIds(ids), BTProtocolPhase.REQUEST);
				sendMessages(eventSet.buildAdvertizeMessagesToSend(ids));
			} else if (phase.equals(BTProtocolPhase.REQUEST)) {
				sendMessages(eventSet);

			}
		} else
			Log.i("Got empty id set", ids.toString());
	}

	private void sendMessages(BTEventSet events) {
		if (!events.isEmpty()) {
			String messages = events.buildUploadMessages();
			mChatService.write(messages.getBytes());

		} else
			Log.i("Tried to sent empty messageMap, failed", events.toString());
	}

	void receivedMessages(BTEventSet messageMap) {
		if (!messageMap.isEmpty())
			for (BTMessage m : messageMap) {
				if (!eventSet.contains(m)) {
					eventSet.add(m);
					Intent intent = new Intent(BTIntent.CHAT_MESSAGE);
					intent.putExtra(BTIntent.CHAT_MESSAGE_PATH,
							"Events we have:" + m.getMessage());
					sendBroadcast(intent);
				}
			}
		else {
			Log.i("Got empty id set", messageMap.toString());
		}
	}

	static BTEventSet parseNewEvents(String[] phaseAndContent) {
		BTEventSet newEvents = new BTEventSet();

		String[] pairs = phaseAndContent[1].split(BTActivity.ID_SEPARATOR);
		for (String pair : pairs) {
			String[] keyAndValue = pair.split(BTActivity.KEY_VALUE_SEPARATOR);
			newEvents.add(new BTMessageImpl(new BTIdImpl(keyAndValue[0]),
					new BTContentImpl(keyAndValue[1])));
		}
		return newEvents;
	}

	static BTIdSet parseReceivedIds(String content) {
		Log.i("content", content);
		String[] ids = content.split(BTActivity.ID_SEPARATOR);

		BTIdSet receivedIds = new BTIdSet();
		for (String s : ids) {
			Log.i("array ids", s);
			receivedIds.add(new BTIdImpl(s));
		}
		Log.i("receivedIds", receivedIds.toString());
		return receivedIds;
	}

	void parseReceivedMessages(String messages) {
		for (String singleMessage : messages
				.split(BTActivity.MESSAGE_SEPARATOR)) {
			String[] phaseAndContent = singleMessage
					.split(BTActivity.PHASE_CONTENT_SEPARATOR);
			BTProtocolPhase phase = BTProtocolPhase.valueOf(phaseAndContent[0]);

			if (phase.equals(BTProtocolPhase.UPLOAD)) {
				BTEventSet newEvents = BTActivity
						.parseNewEvents(phaseAndContent);
				receivedMessages(newEvents);

			} else if ((phase.equals(BTProtocolPhase.REQUEST))
					|| (phase.equals(BTProtocolPhase.ADVERTISE))) {
				BTIdSet receivedIds = BTActivity
						.parseReceivedIds(phaseAndContent[1]);
				receivedIds(receivedIds, phase);
			} else {
				Log.i("Received an unknown message", messages.toString());
			}
		}
	}

	private void setupChat() {

		// Initialize the array adapter for the conversation thread
		mConversationArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.message);
		ListView conversationView = (ListView) findViewById(R.id.device_list);
		conversationView.setAdapter(mConversationArrayAdapter);

		Intent intent = new Intent(BTIntent.CHAT_MESSAGE);
		intent.putExtra(BTIntent.CHAT_MESSAGE_PATH, "Cool debug:");
		sendBroadcast(intent);
		// mConversationArrayAdapter.add("Cool debug");

		// Initialize the compose field with a listener for the return key
		EditText mOutEditText = (EditText) findViewById(R.id.eventNameField);
		mOutEditText.setOnEditorActionListener(mWriteListener);

		Button printEventsButton = (Button) findViewById(R.id.button_scan);
		Button addEventsButton = (Button) findViewById(R.id.addEventButton);
		Button exchangeEventsButton = (Button) findViewById(R.id.exchangeEventsButton);
		exchangeEventsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startExchange();
			}
		});
		printEventsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BTIntent.CHAT_MESSAGE);
				intent.putExtra(BTIntent.CHAT_MESSAGE_PATH, "Events we have:"
						+ eventSet.getMessage());
				sendBroadcast(intent);
				// mConversationArrayAdapter.add("Events we have:"
				// + eventSet.getMessage());
			}
		});
		addEventsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView view = (TextView) findViewById(R.id.eventNameField);
				BTContent content = new BTContentImpl(view.getText().toString());
				BTMessageImpl message = new BTMessageImpl(content);
				eventSet.add(message);

				Intent intent = new Intent(BTIntent.CHAT_MESSAGE);
				intent.putExtra(BTIntent.CHAT_MESSAGE_PATH, "Added event:"
						+ message.getMessage());
				sendBroadcast(intent);
				// mConversationArrayAdapter.add("Added event "
				// + message.getMessage());

				System.out.println("Add events");
			}
		});
	}

	private OnEditorActionListener mWriteListener = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView view, int actionId,
				KeyEvent event) {
			// If the action is a key-up event on the return key, send the
			// message
			if (actionId == EditorInfo.IME_NULL
					&& event.getAction() == KeyEvent.ACTION_UP) {
				String message = view.getText().toString();
				eventSet.add(new BTMessageImpl(new BTIdImpl(),
						new BTContentImpl(message)));

			}
			return true;
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth chat services
		if (mChatService != null)
			mChatService.stop();
	}

	private final Handler mHandler = new BTMessageHandler(this);

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				// Get the device MAC address
				String address = data.getExtras().getString(
						BTActivity.EXTRA_DEVICE_ADDRESS);
				// Get the BLuetoothDevice object
				BluetoothDevice device = mBluetoothAdapter
						.getRemoteDevice(address);
				// Attempt to connect to the device
				mChatService.connect(device);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				setupChat();
			} else {
				// User did not enable Bluetooth or an error occured
				// Toast.makeText(this, R.string.bt_not_enabled_leaving,
				// Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan:
			// Launch the DeviceListActivity to see devices and do scan
			Intent serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			return true;
		case R.id.discoverable:
			// Ensure this device is discoverable by others
			ensureDiscoverable();
			return true;
		}
		return false;
	}

	private void startExchange() {
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			Toast.makeText(this, "Not connected", Toast.LENGTH_SHORT).show();
			return;
		}

		sendIds(eventSet.getKeySet(), BTProtocolPhase.ADVERTISE);
	}

	private void sendIds(BTIdSet ids, BTProtocolPhase phase) {
		String idString = ids.buildIdString(phase);
		Log.i("Sending ids", idString);
		mChatService.write(idString.getBytes());
	}

	private void ensureDiscoverable() {
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
			startActivity(discoverableIntent);
		}
	}
}
