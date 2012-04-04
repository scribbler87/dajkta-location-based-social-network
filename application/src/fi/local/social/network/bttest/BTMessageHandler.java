package fi.local.social.network.bttest;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

final class BTMessageHandler extends Handler {

	/**
	 * 
	 */
	private final BTActivity btActivity;

	/**
	 * @param btActivity
	 */
	BTMessageHandler(BTActivity btActivity) {
		this.btActivity = btActivity;
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case BTActivity.MESSAGE_STATE_CHANGE:
			switch (msg.arg1) {
			case BluetoothChatService.STATE_CONNECTED:
				Intent intent = new Intent(BTIntent.CHAT_CLEAR);
				btActivity.sendBroadcast(intent);
				break;
			case BluetoothChatService.STATE_CONNECTING:
				break;
			case BluetoothChatService.STATE_LISTEN:
				break;
			case BluetoothChatService.STATE_NONE:
				break;
			}
			break;
		case BTActivity.MESSAGE_WRITE:
			byte[] writeBuf = (byte[]) msg.obj;
			// construct a string from the buffer
			String writeMessage = new String(writeBuf);
			Log.i("Handler MESSAGE_WRITE", writeMessage);
			break;
		case BTActivity.MESSAGE_READ:
			byte[] readBuf = (byte[]) msg.obj;
			// construct a string from the valid bytes in the buffer
			String message = new String(readBuf, 0, msg.arg1);
			Log.i("Handler MESSAGE_READ", message);

			btActivity.parseReceivedMessages(message);
			break;
		case BTActivity.MESSAGE_DEVICE_NAME:
			// // save the connected device's name
			// String connectedDeviceName = msg.getData().getString(
			// BTActivity.DEVICE_NAME);
			// Toast.makeText(this.btActivity.getApplicationContext(),
			// "Connected to " + connectedDeviceName, Toast.LENGTH_SHORT)
			// .show();
			break;
		case BTActivity.MESSAGE_TOAST:
			// Toast.makeText(this.btActivity.getApplicationContext(),
			// msg.getData().getString(BTActivity.TOAST),
			// Toast.LENGTH_SHORT).show();
			break;
		}
	}
}