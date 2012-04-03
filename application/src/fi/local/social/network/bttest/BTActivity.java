package fi.local.social.network.bttest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import fi.local.social.network.R;

public class BTActivity extends Activity {

	private final class WriteListener implements
			TextView.OnEditorActionListener {
		public boolean onEditorAction(TextView view, int actionId,
				KeyEvent event) {
			// If the action is a key-up event on the return key, send the
			// message
			if (actionId == EditorInfo.IME_NULL
					&& event.getAction() == KeyEvent.ACTION_UP) {
				String message = view.getText().toString();
				events.put("" + random.nextInt(), message);

			}
			return true;
		}
	}

	static final String PHASE_CONTENT_SEPARATOR = "=";
	static final String KEY_VALUE_SEPARATOR = "#";
	private static final String MESSAGE_SEPARATOR = "£";

	private static final String ADVERTISE = "ADVERTISE";
	private static final String REQUEST = "REQUEST";
	private static final String UPLOAD = "UPLOAD";

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

	Map<String, String> events = new HashMap<String, String>();
	ArrayAdapter<String> mConversationArrayAdapter;
	Random random = new Random();
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothChatService mChatService;

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

	private void setupChat() {

		// Initialize the array adapter for the conversation thread
		mConversationArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.message);
		ListView mConversationView = (ListView) findViewById(R.id.device_list);
		mConversationView.setAdapter(mConversationArrayAdapter);

		mConversationArrayAdapter.add("Cool debug");

		System.out.println("We got here");

		// Initialize the compose field with a listener for the return key
		EditText mOutEditText = (EditText) findViewById(R.id.eventNameField);
		mOutEditText.setOnEditorActionListener(mWriteListener);

		Button printEventsButton = (Button) findViewById(R.id.button_scan);
		Button addEventsButton = (Button) findViewById(R.id.addEventButton);
		Button exchangeEventsButton = (Button) findViewById(R.id.exchangeEventsButton);
		exchangeEventsButton
				.setOnClickListener(new ExchangeEventsListener(this));
		printEventsButton.setOnClickListener(new PrintEventsListener(
				mConversationArrayAdapter, events));
		addEventsButton.setOnClickListener(new AddEventsListener(this));

	}

	private TextView.OnEditorActionListener mWriteListener = new WriteListener();

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth chat services
		if (mChatService != null)
			mChatService.stop();
	}

	private final Handler mHandler = new BTMessageHandler();

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

	void startExchange() {
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			Toast.makeText(this, "Not connected", Toast.LENGTH_SHORT).show();
			return;
		}

		sendIds(events.keySet(), ADVERTISE, mChatService);
	}

	private static void sendIds(Set<String> ids, String phase,
			BluetoothChatService mChatService) {
		if (!ids.isEmpty()) {
			String idString = buildIdString(ids, phase);
			Log.i("Sending ids", idString);
			mChatService.write(idString.getBytes());

		} else
			Log.i("Tried to sent empty id-set, failed", ids.toString());
	}

	private static String buildIdString(Set<String> ids, String phase) {
		StringBuilder idStringBuilder = new StringBuilder();

		if (phase.equals(ADVERTISE))
			idStringBuilder.append(ADVERTISE);
		else if (phase.equals(REQUEST))
			idStringBuilder.append(REQUEST);

		idStringBuilder.append(":");

		for (String id : ids) {
			idStringBuilder.append(id);
			idStringBuilder.append(PHASE_CONTENT_SEPARATOR);
		}
		idStringBuilder.append(MESSAGE_SEPARATOR);
		String idString = idStringBuilder.toString();
		return idString;
	}

	private static void sendMessages(Set<BTMessage> messageSet,
			BluetoothChatService mChatService) {
		if (!messageSet.isEmpty()) {
			String messages = buildMessages(messageSet);
			mChatService.write(messages.getBytes());

		} else
			Log.i("Tried to sent empty messageMap, failed",
					messageSet.toString());
	}

	private static String buildMessages(Set<BTMessage> messageSet) {
		StringBuilder messageStringBuilder = new StringBuilder();
		messageStringBuilder.append(UPLOAD);
		messageStringBuilder.append(":");

		for (BTMessage m : messageSet) {
			messageStringBuilder.append(m.getId());
			messageStringBuilder.append(KEY_VALUE_SEPARATOR);
			messageStringBuilder.append(m.getMessage());
			messageStringBuilder.append(PHASE_CONTENT_SEPARATOR);
		}

		messageStringBuilder.append(MESSAGE_SEPARATOR);
		String messages = messageStringBuilder.toString();
		Log.i("Sending messages", messages);
		return messages;
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

	private final class BTMessageHandler extends Handler {
		private String mConnectedDeviceName = null;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					mConversationArrayAdapter.clear();
					break;
				case BluetoothChatService.STATE_CONNECTING:
					break;
				case BluetoothChatService.STATE_LISTEN:
					break;
				case BluetoothChatService.STATE_NONE:
					break;
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				Log.i("Handler MESSAGE_WRITE", writeMessage);
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				Log.i("Handler MESSAGE_READ", readMessage);

				parseReceivedMessages(readMessage, events,
						mConversationArrayAdapter, mChatService);
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}

		private void parseReceivedMessages(String messages,
				Map<String, String> events,
				ArrayAdapter<String> mConversationArrayAdapter,
				BluetoothChatService mChatService) {
			for (String singleMessage : messages.split(MESSAGE_SEPARATOR)) {
				String[] phaseAndContent = singleMessage.split(":");
				Log.i("phasecontent[0]", phaseAndContent[0]);
				if (phaseAndContent[0].equals(UPLOAD)) {
					Map<String, String> newEvents = parseNewEvents(phaseAndContent);
					receivedMessages(newEvents, events,
							mConversationArrayAdapter);

				} else if ((phaseAndContent[0].equals(REQUEST))
						|| (phaseAndContent[0].equals(ADVERTISE))) {
					Set<String> receivedIds = parseReceivedIds(phaseAndContent);

					receivedIds(receivedIds, phaseAndContent[0], events,
							mChatService);

				} else {
					Log.i("Received an unknown message", messages.toString());
				}
			}
		}

		private Set<String> parseReceivedIds(String[] phaseAndContent) {
			Log.i("phaseContent[1]", phaseAndContent[1]);
			String[] ids = phaseAndContent[1].split(PHASE_CONTENT_SEPARATOR);

			Set<String> receivedIds = new HashSet<String>();
			for (String s : ids) {
				Log.i("array ids", s);
				receivedIds.add(s);
			}
			Log.i("receivedIds", receivedIds.toString());
			return receivedIds;
		}

		private Map<String, String> parseNewEvents(String[] phaseAndContent) {
			Map<String, String> newEvents = new HashMap<String, String>();

			String[] pairs = phaseAndContent[1].split(PHASE_CONTENT_SEPARATOR);
			for (String pair : pairs) {
				String[] keyAndValue = pair.split(KEY_VALUE_SEPARATOR);
				newEvents.put(keyAndValue[0], keyAndValue[1]);
			}
			return newEvents;
		}

		private void receivedIds(Set<String> ids, String phase,
				Map<String, String> events, BluetoothChatService mChatService) {

			if (!ids.isEmpty()) {
				if (phase.equals(ADVERTISE)) {
					Set<String> requestIds = buildRequestIds(ids, events);
					sendIds(requestIds, REQUEST, mChatService);

					Set<BTMessage> messagesToSend = buildAdvertizeMessagesToSend(
							ids, events);

					sendMessages(messagesToSend, mChatService);
				} else if (phase.equals(REQUEST)) {
					Set<BTMessage> messagesToSend = buildRequestMessagesToSend(
							ids, events);
					sendMessages(messagesToSend, mChatService);
				}

			} else
				Log.i("Got empty id set", ids.toString());
		}

		private void receivedMessages(Map<String, String> messageMap,
				Map<String, String> events,
				ArrayAdapter<String> mConversationArrayAdapter) {
			if (!messageMap.isEmpty())
				for (String s : messageMap.keySet()) {
					if (!events.containsKey(s)) {
						events.put(s, messageMap.get(s));
						mConversationArrayAdapter.add("Received event " + s
								+ KEY_VALUE_SEPARATOR + messageMap.get(s));
					}
				}
			else {
				Log.i("Got empty id set", messageMap.toString());
			}
		}
	}

	private static Set<String> buildRequestIds(Set<String> ids,
			Map<String, String> events) {
		Set<String> requestIds = new HashSet<String>();
		// TODO use a filter
		for (String s : ids) {
			if (!events.containsKey(s))
				requestIds.add(s);
		}
		return requestIds;
	}

	private static Set<BTMessage> buildRequestMessagesToSend(Set<String> ids,
			Map<String, String> events) {
		Set<BTMessage> messagesToSend = new HashSet<BTMessage>();
		Set<String> keyset = ids;
		for (String key : keyset) {
			String value = events.get(key);
			messagesToSend.add(new BTMessageImpl(key,value));
		}
		return messagesToSend;
	}

	private static Set<BTMessage> buildAdvertizeMessagesToSend(Set<String> ids,
			Map<String, String> events) {
		Set<BTMessage> messagesToSend = new HashSet<BTMessage>();
		Set<String> keyset = events.keySet();
		for (String key : keyset) {
			// TODO use a filter
			boolean condition = !ids.contains(key);
			if (condition) {
				String value = events.get(key);
				messagesToSend.add(new BTMessageImpl(key, value));
			}
		}
		return messagesToSend;
	}
}
