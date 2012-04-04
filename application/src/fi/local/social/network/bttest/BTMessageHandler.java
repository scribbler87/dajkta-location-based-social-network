package fi.local.social.network.bttest;

import java.util.HashSet;
import java.util.Set;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

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
				this.btActivity.mConversationArrayAdapter.clear();
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

			parseReceivedMessages(message, this.btActivity.eventSet,
					this.btActivity.mConversationArrayAdapter, this.btActivity.mChatService);
			break;
		case BTActivity.MESSAGE_DEVICE_NAME:
			// save the connected device's name
			String connectedDeviceName = msg.getData().getString(BTActivity.DEVICE_NAME);
			Toast.makeText(this.btActivity.getApplicationContext(),
					"Connected to " + connectedDeviceName,
					Toast.LENGTH_SHORT).show();
			break;
		case BTActivity.MESSAGE_TOAST:
			Toast.makeText(this.btActivity.getApplicationContext(),
					msg.getData().getString(BTActivity.TOAST), Toast.LENGTH_SHORT)
					.show();
			break;
		}
	}

	private void parseReceivedMessages(String messages, BTEventSet events,
			ArrayAdapter<String> mConversationArrayAdapter,
			BluetoothChatService mChatService) {
		for (String singleMessage : messages.split(BTActivity.MESSAGE_SEPARATOR)) {
			String[] phaseAndContent = singleMessage
					.split(BTActivity.PHASE_CONTENT_SEPARATOR);
			BTProtocolPhase phase = BTProtocolPhase
					.getPhase(phaseAndContent[0]);

			if (phase.equals(BTActivity.UPLOAD)) {
				BTEventSet newEvents = parseNewEvents(phaseAndContent);
				receivedMessages(newEvents, events,
						mConversationArrayAdapter);

			} else if ((phase.equals(BTActivity.REQUEST)) || (phase.equals(BTActivity.ADVERTISE))) {
				Set<BTId> receivedIds = parseReceivedIds(phaseAndContent[1]);
				receivedIds(receivedIds, phase, events);
			} else {
				Log.i("Received an unknown message", messages.toString());
			}
		}
	}

	private Set<BTId> parseReceivedIds(String content) {
		Log.i("phaseContent[1]", content);
		String[] ids = content.split(BTActivity.ID_SEPARATOR);

		Set<BTId> receivedIds = new HashSet<BTId>();
		for (String s : ids) {
			Log.i("array ids", s);
			receivedIds.add(new BTIdImpl(s));
		}
		Log.i("receivedIds", receivedIds.toString());
		return receivedIds;
	}

	private BTEventSet parseNewEvents(String[] phaseAndContent) {
		BTEventSet newEvents = new BTEventSet();

		String[] pairs = phaseAndContent[1].split(BTActivity.ID_SEPARATOR);
		for (String pair : pairs) {
			String[] keyAndValue = pair.split(BTActivity.KEY_VALUE_SEPARATOR);
			newEvents.add(new BTMessageImpl(new BTIdImpl(keyAndValue[0]),
					new BTContentImpl(keyAndValue[1])));
		}
		return newEvents;
	}

	private void receivedIds(Set<BTId> ids, BTProtocolPhase phase,
			BTEventSet events) {

		if (!ids.isEmpty()) {
			if (phase.equals(BTActivity.ADVERTISE)) {
				Set<BTId> requestIds = BTActivity.buildRequestIds(ids, events);
				this.btActivity.sendIds(requestIds, BTProtocolPhase.REQUEST);

				BTEventSet messagesToSend = BTActivity.buildAdvertizeMessagesToSend(
						ids, events);
				this.btActivity.sendMessages(messagesToSend);
			} else if (phase.equals(BTActivity.REQUEST)) {
				this.btActivity.sendMessages(events);
			}
		} else
			Log.i("Got empty id set", ids.toString());
	}

	private void receivedMessages(BTEventSet messageMap,
			Set<BTMessage> events,
			ArrayAdapter<String> mConversationArrayAdapter) {
		if (!messageMap.isEmpty())
			for (BTMessage m : messageMap) {
				if (!events.contains(m)) {
					events.add(m);
					mConversationArrayAdapter.add("Received event: "
							+ m.getMessage());
				}
			}
		else {
			Log.i("Got empty id set", messageMap.toString());
		}
	}
}