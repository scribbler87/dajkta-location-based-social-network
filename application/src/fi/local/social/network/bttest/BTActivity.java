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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import fi.local.social.network.R;

public class BTActivity extends Activity {

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
	private Button printEventsButton;
	private Button addEventsButton;
	private Button exchangeEventsButton;
	private Map<String, String> events = new HashMap<String, String>();
	private ArrayAdapter<String> mConversationArrayAdapter;
	private int idSeq = 0;
	private Random random = new Random();
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothChatService mChatService;
	private ListView mConversationView;
	private EditText mOutEditText;
	private StringBuffer mOutStringBuffer;
	private String mConnectedDeviceName = null;

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
		mConversationView = (ListView) findViewById(R.id.device_list);
		mConversationView.setAdapter(mConversationArrayAdapter);

		mConversationArrayAdapter.add("Cool debug");

		System.out.println("We got here");

		// Initialize the compose field with a listener for the return key
		mOutEditText = (EditText) findViewById(R.id.eventNameField);
		mOutEditText.setOnEditorActionListener(mWriteListener);

		printEventsButton = (Button) findViewById(R.id.button_scan);
		addEventsButton = (Button) findViewById(R.id.addEventButton);
		exchangeEventsButton = (Button) findViewById(R.id.exchangeEventsButton);

		printEventsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				StringBuilder allEvents = new StringBuilder();
				allEvents.append("Events we have:");
				for (String s : events.keySet()) {
					allEvents.append("" + s + "#" + events.get(s) + "=");
				}
				mConversationArrayAdapter.add(allEvents.toString());
			}
		});
		addEventsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView view = (TextView) findViewById(R.id.eventNameField);
				String message = view.getText().toString();
				events.put("" + idSeq, message);
				mConversationArrayAdapter.add("Added event " + idSeq + "#"
						+ message);
				idSeq = random.nextInt();

				System.out.println("Add events");
			}
		});
		exchangeEventsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startExchange();
			}

		});
	}

	private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener() {
		public boolean onEditorAction(TextView view, int actionId,
				KeyEvent event) {
			// If the action is a key-up event on the return key, send the
			// message
			if (actionId == EditorInfo.IME_NULL
					&& event.getAction() == KeyEvent.ACTION_UP) {
				String message = view.getText().toString();
				events.put("" + idSeq, message);
				idSeq = random.nextInt();

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

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					// mTitle.setText(R.string.title_connected_to);
					// mTitle.append(mConnectedDeviceName);
					mConversationArrayAdapter.clear();
					break;
				case BluetoothChatService.STATE_CONNECTING:
					// mTitle.setText(R.string.title_connecting);
					break;
				case BluetoothChatService.STATE_LISTEN:
				case BluetoothChatService.STATE_NONE:
					// mTitle.setText(R.string.title_not_connected);
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
				parseReceivedString(readMessage);
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

	};

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

		sendIds(events.keySet(), ADVERTISE);
	}

	private void parseReceivedString(String messages) {
		for (String singleMessage : messages.split("£")) {
			String[] phaseAndContent = singleMessage.split(":");
			Log.i("phasecontent[0]", phaseAndContent[0]);
			if (phaseAndContent[0].equals(UPLOAD)) {
				Map<String, String> newEvents = new HashMap<String, String>();

				String[] pairs = phaseAndContent[1].split("=");
				for (String pair : pairs) {
					String[] keyAndValue = pair.split("#");
					newEvents.put(keyAndValue[0], keyAndValue[1]);
				}
				receivedMessages(newEvents);

			} else if ((phaseAndContent[0].equals(REQUEST))
					|| (phaseAndContent[0].equals(ADVERTISE))) {
				Log.i("phaseContent[1]", phaseAndContent[1]);
				String[] ids = phaseAndContent[1].split("=");

				Set<String> receivedIds = new HashSet<String>();
				for (String s : ids) {
					Log.i("array ids", s);
					receivedIds.add(s);
				}
				Log.i("receivedIds", receivedIds.toString());
				receivedIds(receivedIds, phaseAndContent[0]);

			} else {
				Log.i("Received an unknown message", messages.toString());
			}
		}
	}

	private void sendIds(Set<String> ids, String phase) {
		if (!ids.isEmpty()) {
			StringBuilder idString = new StringBuilder();

			if (phase.equals(ADVERTISE))
				idString.append(ADVERTISE);
			else if (phase.equals(REQUEST))
				idString.append(REQUEST);

			idString.append(":");

			for (String s : ids) {
				idString.append(s);
				idString.append('=');
			}
			idString.append("£");
			Log.i("Sending ids", idString.toString());
			mChatService.write(idString.toString().getBytes());

			// Reset out string buffer to zero
			mOutStringBuffer.setLength(0);

		} else
			Log.i("Tried to sent empty id-set, failed", ids.toString());
	}

	private void sendMessages(Map<String, String> messageMap) {
		if (!messageMap.isEmpty()) {
			StringBuilder message = new StringBuilder();

			message.append(UPLOAD);
			message.append(":");

			for (String s : messageMap.keySet()) {
				Log.i("sendMessages ids", s);
				message.append(s);
				message.append("#");
				message.append(messageMap.get(s));
				Log.i("sendMessages messages", messageMap.get(s));
				message.append("=");
			}
			message.append("£");
			Log.i("Sending messages", message.toString());
			mChatService.write(message.toString().getBytes());

			// Reset out string buffer to zero
			mOutStringBuffer.setLength(0);

		} else
			Log.i("Tried to sent empty messageMap, failed",
					messageMap.toString());
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

	private void receivedIds(Set<String> ids, String phase) {
		if (!ids.isEmpty()) {
			if (phase.equals(ADVERTISE)) {
				Set<String> requestIds = new HashSet<String>();
				for (String s : ids) {
					if (!events.containsKey(s))
						requestIds.add(s);
				}
				sendIds(requestIds, REQUEST);

				Map<String, String> messagesToSend = new HashMap<String, String>();
				for (String s : events.keySet()) {
					if (!ids.contains(s))
						messagesToSend.put(s, events.get(s));
				}
				sendMessages(messagesToSend);
			} else if (phase.equals(REQUEST)) {
				Map<String, String> messagesToSend = new HashMap<String, String>();
				for (String s : ids)
					messagesToSend.put(s, events.get(s));

				sendMessages(messagesToSend);
			}

		} else
			Log.i("Got empty id set", ids.toString());
	}

	private void receivedMessages(Map<String, String> messageMap) {
		if (!messageMap.isEmpty())
			for (String s : messageMap.keySet()) {
				if (!events.containsKey(s)) {
					events.put(s, messageMap.get(s));
					mConversationArrayAdapter.add("Received event " + s + "#"
							+ messageMap.get(s));
				}
			}
		else {
			Log.i("Got empty id set", messageMap.toString());
		}
	}
}
