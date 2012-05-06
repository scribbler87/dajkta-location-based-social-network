package fi.local.social.network.btservice;

import java.util.HashMap;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BroadCastReceiverDevices extends BroadcastReceiver{

	private HashMap<String,String> devicesAddr;

	public BroadCastReceiverDevices() {
		devicesAddr = new HashMap<String, String>();
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		// When discovery finds a device
		if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			// Get the BluetoothDevice object from the Intent
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			// If it's already paired, skip it, because it's been listed already
			String deviceName = device.getName();
			if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
				String address = device.getAddress();
				System.err.println((deviceName + "\n" + address));
				
				devicesAddr.put(address,deviceName);
				
				// send the founded devices back to gui
				BTService.sendAddrToPeopleActivity(address,deviceName);
				
				// TODO later start here a different service or thread which gets the userinformation
				// such as username and uri to the pic
			}
			else
			{
				String address = device.getAddress();
				System.err.println((deviceName + "\n" + address));
				devicesAddr.put(address,deviceName);
				
				// send the founded devices back to gui
				BTService.sendAddrToPeopleActivity(address,deviceName);
			}
			// When discovery is finished, change the Activity title
		} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
			if (devicesAddr.size() == 0) {
				//	String noDevices = getResources().getText(R.string.none_found).toString();
				//System.err.println("no device");
				Toast.makeText(context.getApplicationContext(), "No device founded at the moment.", Toast.LENGTH_SHORT);
			}

		}

	}
}
