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

	static final String PHASE_CONTENT_SEPARATOR = ":";

	private final class WriteListener implements
			TextView.OnEditorActionListener {
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
	}

	static final String ID_SEPARATOR = "=";
	static final String KEY_VALUE_SEPARATOR = "#";
	static final String MESSAGE_SEPARATOR = "£";

	static final String ADVERTISE = "ADVERTISE";
	static final String REQUEST = "REQUEST";
	static final String UPLOAD = "UPLOAD";

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

	// private Map<BTId, BTContent> events = new HashMap<BTId, BTContent>();
	BTEventSet eventSet = new BTEventSet();
	ArrayAdapter<String> mConversationArrayAdapter;
	Random random = new Random();
	private BluetoothAdapter mBluetoothAdapter;
	BluetoothChatService mChatService;

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
		printEventsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mConversationArrayAdapter.add("Events we have:"
						+ eventSet.getMessage());
			}
		});
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

	void startExchange() {
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			Toast.makeText(this, "Not connected", Toast.LENGTH_SHORT).show();
			return;
		}

		sendIds(eventSet.getKeySet(), BTProtocolPhase.ADVERTISE);
	}

	void sendIds(Set<BTId> ids, BTProtocolPhase phase) {
		if (!ids.isEmpty()) {
			String idString = buildIdString(ids, phase);
			Log.i("Sending ids", idString);
			mChatService.write(idString.getBytes());

		} else
			Log.i("Tried to sent empty id-set, failed", ids.toString());
	}

	private static String buildIdString(Set<BTId> ids, BTProtocolPhase phase) {
		StringBuilder idStringBuilder = new StringBuilder();

		idStringBuilder.append(phase.getMessage());
		idStringBuilder.append(PHASE_CONTENT_SEPARATOR);

		for (BTId id : ids) {
			idStringBuilder.append(id.getMessage());
			idStringBuilder.append(ID_SEPARATOR);
		}
		idStringBuilder.append(MESSAGE_SEPARATOR);
		String idString = idStringBuilder.toString();
		return idString;
	}

	void sendMessages(BTEventSet messageSet) {
		if (!messageSet.isEmpty()) {
			String messages = buildUploadMessages(messageSet);
			mChatService.write(messages.getBytes());

		} else
			Log.i("Tried to sent empty messageMap, failed",
					messageSet.toString());
	}

	private static String buildUploadMessages(BTEventSet messageSet) {
		StringBuilder messageStringBuilder = new StringBuilder();
		messageStringBuilder.append(UPLOAD);
		messageStringBuilder.append(PHASE_CONTENT_SEPARATOR);
		messageStringBuilder.append(messageSet.getMessage());
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

	static Set<BTId> buildRequestIds(Set<BTId> ids, BTEventSet events) {
		Set<BTId> requestIds = new HashSet<BTId>();
		Filter<BTId> filter;
		for (BTId id : ids) {
			if (!events.getKeySet().contains(id))
				requestIds.add(id);
		}
		return requestIds;
	}

	static BTEventSet buildAdvertizeMessagesToSend(Set<BTId> ourIds,
			BTEventSet ourEvents) {
		Filter<BTMessage> filter = new IdSetEventFilter(ourIds);

		BTEventSet messagesToSend = new BTEventSet();
		for (BTId id : ourIds) {
			BTMessage m = ourEvents.get(id);
			if (m != null) {
				messagesToSend.add(m);
			}
		}
		return messagesToSend;
	}

	BTEventSet getEvents() {
		return eventSet;
	}
}
