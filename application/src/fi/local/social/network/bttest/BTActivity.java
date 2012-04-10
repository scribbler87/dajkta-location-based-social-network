package fi.local.social.network.bttest;

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

	private final BroadcastReceiver eventBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.equals(BTIntent.EVENT_ADD)) {
				BTMessage e = (BTMessage) intent
						.getSerializableExtra(BTIntent.EVENT_ADD_PATH);
				eventSet.add(e);

				Intent chatIntent = new Intent(BTIntent.CHAT_MESSAGE);
				intent.putExtra(BTIntent.CHAT_MESSAGE_PATH, "Events we have:"
						+ e.getMessage());
				sendBroadcast(chatIntent);
			}
		}
	};

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

	private final BroadcastReceiver eventReceiveBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String messageString = intent
					.getStringExtra(BTIntent.EVENT_RECEIVE_PATH);
			String[] messages = messageString
					.split(BTActivity.MESSAGE_SEPARATOR);
			for (String singleMessage : messages) {
				String[] phaseAndContent = singleMessage
						.split(BTActivity.PHASE_CONTENT_SEPARATOR);
				BTProtocolPhase phase = BTProtocolPhase
						.valueOf(phaseAndContent[0]);
				String content = phaseAndContent[1];

				if (phase.equals(BTProtocolPhase.UPLOAD)) {
					BTEventSet newEvents = new BTEventSet(content);
					BTEventSet toAdd = eventSet.notContained(newEvents);
					if (!toAdd.isEmpty()) {
						for (BTMessage e : toAdd) {
							Intent addEventIntent = new Intent(BTIntent.EVENT_ADD);
							addEventIntent.putExtra(BTIntent.EVENT_ADD_PATH, e);
						}
					}
				} else if ((phase.equals(BTProtocolPhase.REQUEST))
						|| (phase.equals(BTProtocolPhase.ADVERTISE))) {
					BTIdSet receivedIds = new BTIdSet(content);
					receivedIds(receivedIds, phase);
				} else {
					Log.i("Received an unknown message",
							messageString.toString());
				}
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

		IntentFilter chatFilter = new IntentFilter(
				BTIntent.CHAT_MESSAGE.getAction());
		chatFilter.addAction(BTIntent.CHAT_MESSAGE.getAction());
		chatFilter.addAction(BTIntent.CHAT_CLEAR.getAction());
		registerReceiver(chatMessageBroadcastReceiver, chatFilter);

		IntentFilter eventFilter = new IntentFilter(
				BTIntent.EVENT_ADD.getAction());
		eventFilter.addAction(BTIntent.EVENT_ADD.getAction());
		registerReceiver(eventBroadcastReceiver, eventFilter);

		IntentFilter eventReceiveFilter = new IntentFilter(
				BTIntent.EVENT_RECEIVE.getAction());
		eventReceiveFilter.addAction(BTIntent.EVENT_RECEIVE.getAction());
		registerReceiver(eventReceiveBroadcastReceiver, eventReceiveFilter);

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
				mChatService.write(eventSet.buildRequestIds(ids)
						.buildIdString(BTProtocolPhase.REQUEST).getBytes());
				sendMessages(eventSet.buildAdvertizeMessagesToSend(ids));
			} else if (phase.equals(BTProtocolPhase.REQUEST)) {
				sendMessages(eventSet);

			}
		}
	}

	private void sendMessages(BTEventSet events) {
		if (!events.isEmpty()) {
			String messages = events.buildUploadMessages();
			mChatService.write(messages.getBytes());

		} else
			Log.i("Tried to sent empty messageMap, failed", events.toString());
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

		// Initialize the compose field with a listener for the return key
		EditText mOutEditText = (EditText) findViewById(R.id.eventNameField);
		mOutEditText.setOnEditorActionListener(new OnEditorActionListener() {

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
		});

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

				System.out.println("Add events");
			}
		});
	}

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

		mChatService.write(eventSet.getKeySet()
				.buildIdString(BTProtocolPhase.ADVERTISE).getBytes());
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
