/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fi.local.social.network.btservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import fi.local.social.network.R;
import fi.local.social.network.activities.ChatActivity;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BluetoothChatService extends Service
{

	/** Keeps track of all current registered clients. */
	ArrayList<Messenger> mClients = new ArrayList<Messenger>();

	/**
	 * Command to the service to register a client, receiving callbacks
	 * from the service.  The Message's replyTo field must be a Messenger of
	 * the client where callbacks should be sent.
	 */
	public static final int MSG_REGISTER_CLIENT = 1;

	/**
	 * Command to the service to unregister a client, ot stop receiving callbacks
	 * from the service.  The Message's replyTo field must be a Messenger of
	 * the client as previously given with MSG_REGISTER_CLIENT.
	 */
	public static final int MSG_UNREGISTER_CLIENT = 2;


	public static final int MSG_SEND_EVENT = 10;
	public static final int MSG_REC_EVENT = 10;

	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothChatService mChatService = null;

	// Intent request codes
	public static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;

	static final int MSG_SET_INT_VALUE = 3;
	public static final int MSG_DEVICE_ADDRESS = 15;
	static final int MSG_SET_STRING_VALUE = 4;


	/**
	 * Handler of incoming messages from clients.
	 */
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REGISTER_CLIENT:
				mClients.add(msg.replyTo);
				break;
			case MSG_UNREGISTER_CLIENT:
				mClients.remove(msg.replyTo);
				break;
			case MSG_SEND_EVENT:
				write("Hello World".getBytes());
				break;//BluetoothDevice device = mBluetoothAdapter
//				.getRemoteDevice(address);
			case MSG_DEVICE_ADDRESS:
				String deviceAdress = (String) msg.getData().get("address");
				BluetoothDevice device = mBluetoothAdapter
						.getRemoteDevice(deviceAdress);
				
				// TODO connect
			default:
				super.handleMessage(msg);
			}
		}
	}


	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	final Messenger mMessenger = new Messenger(new IncomingHandler());

	@Override
	public void onCreate()
	{
		// Tell the user we started.
		Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
			return;
		}
		
		// TODO: Maybe make this more beautiful. .enable() is a hack, should probably ask the user for permission
		
		
		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter.enable();
		}
		
		if (mChatService == null)
			setupChat();
	}

	private void setupChat() {

		// Initialize the array adapter for the conversation thread
		//		mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
		//		mConversationView = (ListView) findViewById(R.id.in);
		//		mConversationView.setAdapter(mConversationArrayAdapter);
		//
		//		mConversationArrayAdapter.add("Cool debug");
		//
		//		System.out.println("We got here");
		//
		//		// Initialize the compose field with a listener for the return key
		//		mOutEditText = (EditText) findViewById(R.id.edit_text_out);
		//		mOutEditText.setOnEditorActionListener(mWriteListener);
		//
		//		printEventsButton = (Button) findViewById(R.id.button_printevents);
		//		addEventsButton = (Button) findViewById(R.id.button_event);
		//		exchangeEventsButton = (Button) findViewById(R.id.button_exchange);
		//
		//		printEventsButton.setOnClickListener(new OnClickListener() {

		//			@Override
		//			public void onClick(View v) {
		//				StringBuilder allEvents = new StringBuilder();
		//				allEvents.append("Events we have:");
		//				for (String s : events.keySet()) {
		//					allEvents.append("" + s + ID_EVENT_SEPARATOR
		//							+ events.get(s) + PHASE_CONTENT_SEPARATOR);
		//				}
		//				mConversationArrayAdapter.add(allEvents.toString());
		//			}
		//		});
		//		addEventsButton.setOnClickListener(new OnClickListener() {

		//			@Override
		//			public void onClick(View v) {
		//				TextView view = (TextView) findViewById(R.id.edit_text_out);
		//				String message = view.getText().toString();
		//				events.put("" + idSeq, message);
		//				mConversationArrayAdapter.add("Added event " + idSeq
		//						+ ID_EVENT_SEPARATOR + message);
		//				idSeq = random.nextInt();
		//				System.out.println("Added events");
		//			}
		//		});
		//		exchangeEventsButton.setOnClickListener(new OnClickListener() {

		//			@Override
		//			public void onClick(View v) {
		//				startExchange();
		//			}

		//		});

		// Initialize the BluetoothChatService to perform bluetooth connections
//		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = STATE_NONE;
		//mHandler = handler;
	}


	@Override
	public void onDestroy()
	{
		// Tell the user we stopped.
		Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
	}

	/**
	 * When binding to the service, we return an interface to our messenger
	 * for sending messages to the service.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}

	
	
	
	////////////////////////////////////

	// Debugging
	private static final String TAG = "MobileNeighbourService";
	private static final boolean D = true;

	// Name for the SDP record when creating server socket
	private static final String NAME = "MobileNeighbour";

	// TODO: Change this
	// Unique UUID for this application
	private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

	// Member fields
	private final BluetoothAdapter mAdapter = null;
	private AcceptThread mAcceptThread;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	private int mState = -1;

	// Constants that indicate the current connection state
	public static final int STATE_NONE = 0;       // we're doing nothing
	public static final int STATE_LISTEN = 1;     // now listening for incoming connections
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
	public static final int STATE_CONNECTED = 3;  // now connected to a remote device

	/**
	 * Constructor. Prepares a new BluetoothChat session.
	 * @param context  The UI Activity Context
	 * @param handler  A Handler to send messages back to the UI Activity
	 */
//	public BluetoothChatService(Context context, Handler handler) {
//		mAdapter = BluetoothAdapter.getDefaultAdapter();
//		mState = STATE_NONE;
//		mHandler = handler;
//	}

	/**connect
	 * Set the current state of the chat connection
	 * @param state  An integer defining the current connection state
	 */
	private synchronized void setState(int state) {
		if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
		mState = state;

		// Give the new state to the Handler so the UI Activity can update
		// TODO send to activity
	}

	/**
	 * Return the current connection state. */
	public synchronized int getState() {
		return mState;
	}

	/**
	 * Start the chat service. Specifically start AcceptThread to begin a
	 * session in listening (server) mode. Called by the Activity onResume() */
	public synchronized void start() {
		if (D) Log.d(TAG, "start");

		// Cancel any thread attempting to make a connection
		if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

		// Start the thread to listen on a BluetoothServerSocket
		if (mAcceptThread == null) {
			mAcceptThread = new AcceptThread();
			mAcceptThread.start();
		}
		setState(STATE_LISTEN);
	}

	/**
	 * Start the ConnectThread to initiate a connection to a remote device.
	 * @param device  The BluetoothDevice to connect
	 */
	public synchronized void connect(BluetoothDevice device) {
		if (D) Log.d(TAG, "connect to: " + device);

		// Cancel any thread attempting to make a connection
		if (mState == STATE_CONNECTING) {
			if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

		// Start the thread to connect with the given device
		mConnectThread = new ConnectThread(device);
		mConnectThread.start();
		setState(STATE_CONNECTING);
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection
	 * @param socket  The BluetoothSocket on which the connection was made
	 * @param device  The BluetoothDevice that has been connected
	 */
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
		if (D) Log.d(TAG, "connected");

		// Cancel the thread that completed the connection
		if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

		// Cancel the accept thread because we only want to connect to one device
		if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}

		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();

		// Send the name of the connected device back to the UI Activity
		// TODO send to activity

		setState(STATE_CONNECTED);
	}

	/**
	 * Stop all threads
	 */
	public synchronized void stop() {
		if (D) Log.d(TAG, "stop");
		if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
		if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
		if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}
		setState(STATE_NONE);
	}

	/**
	 * Write to the ConnectedThread in an unsynchronized manner
	 * @param out The bytes to write
	 * @see ConnectedThread#write(byte[])
	 */
	public void write(byte[] out) {
		// Create temporary object
		ConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (mState != STATE_CONNECTED) return;
			r = mConnectedThread;
		}
		// Perform the write unsynchronized
		r.write(out);
	}

	/**
	 * Indicate that the connection attempt failed and notify the UI Activity.
	 */
	private void connectionFailed() {
		setState(STATE_LISTEN);

		// Send a failure message back to the Activity
		// TODO send to activity
	}

	/**
	 * Indicate that the connection was lost and notify the UI Activity.
	 */
	private void connectionLost() {
		setState(STATE_LISTEN);
		this.stop();
		this.start();
		// Send a failure message back to the Activity
		// TODO send to activity
	}

	/**
	 * This thread runs while listening for incoming connections. It behaves
	 * like a server-side client. It runs until a connection is accepted
	 * (or until cancelled).
	 */
	private class AcceptThread extends Thread {
		// The local server socket
		private final BluetoothServerSocket mmServerSocket;

		public AcceptThread() {
			BluetoothServerSocket tmp = null;

			// Create a new listening server socket
			try {
				tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
			} catch (IOException e) {
				Log.e(TAG, "listen() failed", e);
			}
			mmServerSocket = tmp;
		}

		public void run() {
			if (D) Log.d(TAG, "BEGIN mAcceptThread" + this);
			setName("AcceptThread");
			BluetoothSocket socket = null;

			// Listen to the server socket if we're not connectedeplyTo
			while (mState != STATE_CONNECTED) {
				try {
					// This is a blocking call and will only return on a
					// successful connection or an exception
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					Log.e(TAG, "accept() failed", e);
					break;
				}

				// If a connection was accepted
				if (socket != null) {
					synchronized (BluetoothChatService.this) {
						switch (mState) {
						case STATE_LISTEN:
						case STATE_CONNECTING:
							// Situation normal. Start the connected thread.
							connected(socket, socket.getRemoteDevice());
							break;
						case STATE_NONE:
						case STATE_CONNECTED:
							// Either not ready or already connected. Terminate new socket.
							try {
								socket.close();
							} catch (IOException e) {
								Log.e(TAG, "Could not close unwanted socket", e);
							}
							break;
						}
					}
				}
			}
			if (D) Log.i(TAG, "END mAcceptThread");
		}

		public void cancel() {
			if (D) Log.d(TAG, "cancel " + this);
			try {
				mmServerSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of server failed", e);
			}
		}
	}


	/**
	 * This thread runs while attempting to make an outgoing connection
	 * with a device. It runs straight through; the connection either
	 * succeeds or fails.
	 */
	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			mmDevice = device;
			BluetoothSocket tmp = null;

			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			try {
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
				Log.e(TAG, "create() failed", e);
			}
			mmSocket = tmp;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectThread");
			setName("ConnectThread");

			// Always cancel discovery because it will slow down a connection
			mAdapter.cancelDiscovery();

			// Make a connection to the BluetoothSocket
			try {
				// This is a blocking call and will only return on a
				// successful connection or an exception
				mmSocket.connect();
			} catch (IOException e) {
				connectionFailed();
				// Close the socket
				try {
					mmSocket.close();
				} catch (IOException e2) {
					Log.e(TAG, "unable to close() socket during connection failure", e2);
				}
				// Start the service over to restart listening mode
				BluetoothChatService.this.start();
				return;
			}

			// Reset the ConnectThread because we're done
			synchronized (BluetoothChatService.this) {
				mConnectThread = null;
			}

			// Start the connected thread
			connected(mmSocket, mmDevice);
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}

	/**
	 * This thread runs during a connection with a remote device.
	 * It handles all incoming and outgoing transmissions.
	 */
	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			Log.d(TAG, "create ConnectedThread");
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, "temp sockets not created", e);
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectedThread");
			byte[] buffer = new byte[1024];
			int bytes;

			// Keep listening to the InputStream while connected
			while (true) {
				try {
					// Read from the InputStream
					bytes = mmInStream.read(buffer);

					// Send the obtained bytes to the UI Activity
					//mHandler.obtainMessage(BTActivity.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
					Message message = Message.obtain(null, BluetoothChatService.MSG_REC_EVENT);
					Bundle b = new Bundle();
					b.putString("str1", buffer.toString());
					message.setData(b);
					mClients.get(0).send(message);
					Log.i(TAG, buffer.toString());
				} catch (Exception e) {
					Log.e(TAG, "disconnected", e);
					connectionLost();
					break;
				} // TODO add more excptions
			}
		}

		/**
		 * Write to the connected OutStream.
		 * @param buffer  The bytes to write
		 */
		public void write(byte[] buffer) {
			try {
				mmOutStream.write(buffer);

				// Share the sent message back to the UI Activity
				//mHandler.obtainMessage(BTActivity.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
				// TODO: todo
				Log.i(TAG, buffer.toString());
			} catch (IOException e) {
				Log.e(TAG, "Exception during write", e);
			}
		}
		

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}
	
	
	
	
	
}
