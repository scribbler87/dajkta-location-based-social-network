package fi.local.social.network.btservice;

import java.util.ArrayList;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;

public class BTService extends Service{


	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_UNREGISTER_CLIENT = 2;
	public static final int MSG_SEND_EVENT = 3;
	public static final int MSG_REC_EVENT = 4;

	private BluetoothAdapter mBluetoothAdapter = null;

	private ArrayList<String> devicesAddr;

	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	final Messenger mMessenger = new Messenger(new IncomingHandler());
	/** Keeps track of all current registered clients. */
	ArrayList<Messenger> mClients = new ArrayList<Messenger>();
	private String TAG = "btservice";
	private IntentFilter intFilter;
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
				// TODO send to device
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// is the bluetooth turned on?
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// turn bt on if it not turned on
		if(!mBluetoothAdapter.isEnabled())
			mBluetoothAdapter.enable();


		mBluetoothAdapter.startDiscovery();
		


		intFilter = new IntentFilter();
		intFilter.addAction(BluetoothDevice.ACTION_FOUND);
		intFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		intFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);


		// Register for broadcasts when a device is discovered
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(broadCastReceiver, intFilter);

		// Register for broadcasts when discovery has finished
		//		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		//		this.registerReceiver(broadCastReceiver, filter);

		doDiscovery();

		// send the founded devices to the peopleactivity

	}

	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mBluetoothAdapter.cancelDiscovery();
	}

	/**
	 * Start device discover with the BluetoothAdapter
	 */
	private void doDiscovery() {
		Log.d(TAG , "doDiscovery()");



		// If we're already discovering, stop it
		if (mBluetoothAdapter.isDiscovering()) {
			mBluetoothAdapter.cancelDiscovery();
		}

		// Request discover from BluetoothAdapter
		mBluetoothAdapter.startDiscovery();
	}

	// search for devices binded and not binded
	private static final BroadcastReceiver broadCastReceiver = new BroadCastReceiverDevices(); 




}
