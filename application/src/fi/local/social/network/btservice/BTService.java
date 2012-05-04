package fi.local.social.network.btservice;

import java.util.ArrayList;

import fi.local.social.network.activities.PeopleActivity;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

public class BTService extends Service{


	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_UNREGISTER_CLIENT = 2;
	public static final int MSG_SEND_EVENT = 3;
	public static final int MSG_REC_EVENT = 4;
	public static final int MSG_NEW_ADDR = 5;
	public static final int MSG_START_DISCOVERY = 6;
	public static final int MSG_REC_MESSAGE = 7; 

	private BluetoothAdapter mBluetoothAdapter = null;

	private ArrayList<String> devicesAddr;

	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	final Messenger mMessenger = new Messenger(new IncomingHandler());
	/** Keeps track of all current registered clients. */
	public static ArrayList<Messenger> mClients = new ArrayList<Messenger>();
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
			case MSG_START_DISCOVERY:
				doDiscovery();
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
		

		// define filter for broadcast
		intFilter = new IntentFilter();
		intFilter.addAction(BluetoothDevice.ACTION_FOUND);
		intFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		intFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);


		// Register for broadcasts when a device is discovered
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(broadCastReceiver, intFilter);

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
	
	
	// send the founded devices to the peopleactivity
	public static void sendAddrToPeopleActivity(String addr) {
		System.err.println("addres: " + addr);
		for(int i = 0; i < mClients.size() ; i++)
		{
			Messenger client = mClients.get(i);
			
			Bundle b = new Bundle();
			b.putString("address", addr);
			Message msg = Message.obtain(null, MSG_NEW_ADDR);
			msg.setData(b);
			try {
				client.send(msg);
			} catch (RemoteException e) {
				// TODO mClients.remove(i);?? 
				e.printStackTrace();
			}
		}

    }




}
