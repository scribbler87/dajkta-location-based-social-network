package fi.local.social.network.btservice;

import java.util.ArrayList;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadCastReceiverDevices extends BroadcastReceiver{

	private ArrayList<String> devicesAddr;

	public BroadCastReceiverDevices() {
		devicesAddr = new ArrayList<String>();
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		// When discovery finds a device
		if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			// Get the BluetoothDevice object from the Intent
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			// If it's already paired, skip it, because it's been listed already
			if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
				System.err.println((device.getName() + "\n" + device.getAddress()));
				devicesAddr.add(device.getAddress());
			}
			// When discovery is finished, change the Activity title
		} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
			if (devicesAddr.size() == 0) {
				//	String noDevices = getResources().getText(R.string.none_found).toString();
				System.err.println("no device");
			}

		}

	}
}
