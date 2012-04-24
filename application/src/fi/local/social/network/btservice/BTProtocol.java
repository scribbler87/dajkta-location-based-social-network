//package fi.local.social.network.btservice;
//
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
//import android.util.Log;
//
//public class BTProtocol
//{
//	private static final String PAIR_SEPARATOR = "%";
//	private static final String PHASE_CONTENT_SEPARATOR = "=";
//	private static final String ID_EVENT_SEPARATOR = "#";
//	private static final String MESSAGE_SEPARATOR = ":";
//
//	private static final String ADVERTISE = "ADVERTISE";
//	private static final String REQUEST = "REQUEST";
//	private static final String UPLOAD = "UPLOAD";
//	
//	private void parseReceivedString(String messages) {
//		for (String singleMessage : messages.split(MESSAGE_SEPARATOR)) {
//			String[] phaseAndContent = singleMessage
//					.split(PHASE_CONTENT_SEPARATOR);
//			Log.i("phasecontent[0]", phaseAndContent[0]);
//			if (phaseAndContent[0].equals(UPLOAD)) {
//				// Map<String, String> newEvents = new HashMap<String,
//				// String>();
//				//
//				// String[] pairs = phaseAndContent[1].split("=");
//				// for (String pair : pairs) {
//				// String[] keyAndValue = pair.split("#");
//				// newEvents.put(keyAndValue[0], keyAndValue[1]);
//				// }
//				Map<String, String> newEvents = parseNewEvents(phaseAndContent);
//				receivedMessages(newEvents);
//
//			} else if ((phaseAndContent[0].equals(REQUEST))
//					|| (phaseAndContent[0].equals(ADVERTISE))) {
//				Set<String> receivedIds = parseReceivedIds(phaseAndContent);
//				// Log.i("phaseContent[1]", phaseAndContent[1]);
//				// String[] ids = phaseAndContent[1].split("=");
//				//
//				// Set<String> receivedIds = new HashSet<String>();
//				// for (String s : ids) {
//				// Log.i("array ids", s);
//				// receivedIds.add(s);
//				// }
//
//				Log.i("receivedIds", receivedIds.toString());
//				receivedIds(receivedIds, phaseAndContent[0]);
//
//			} else {
//				Log.i("Received an unknown message", messages.toString());
//			}
//		}
//	}
//
//	private Set<String> parseReceivedIds(String[] phaseAndContent) {
//		assert phaseAndContent.length > 1;
//		Log.i("phaseContent[1]", phaseAndContent[1]);
//		String[] ids = phaseAndContent[1].split(ID_EVENT_SEPARATOR);
//		Set<String> receivedIds = new HashSet<String>();
//		for (String s : ids) {
//			Log.i("array ids", s);
//			receivedIds.add(s);
//		}
//		return receivedIds;
//	}
//
//	private Map<String, String> parseNewEvents(String[] phaseAndContent) {
//		Map<String, String> newEvents = new HashMap<String, String>();
//		String[] pairs = phaseAndContent[1].split(PAIR_SEPARATOR);
//		for (String pair : pairs) {
//			String[] keyAndValue = pair.split(ID_EVENT_SEPARATOR);
//			newEvents.put(keyAndValue[0], keyAndValue[1]);
//
//		}
//		return newEvents;
//	}
//
//	private void sendIds(Set<String> ids, String phase) {
//		if (!ids.isEmpty()) {
//			StringBuilder idString = new StringBuilder();
//
//			if (phase.equals(ADVERTISE))
//				idString.append(ADVERTISE);
//			else if (phase.equals(REQUEST))
//				idString.append(REQUEST);
//
//			idString.append(PHASE_CONTENT_SEPARATOR);
//
//			for (String s : ids) {
//				idString.append(s);
//				idString.append(ID_EVENT_SEPARATOR);
//			}
//			idString.append(MESSAGE_SEPARATOR);
//			Log.i("Sending ids", idString.toString());
//			BluetoothChatService.write(idString.toString().getBytes());
//
//		} else
//			Log.i("Tried to sent empty id-set, failed", ids.toString());
//	}
//
//	private void sendMessages(Map<String, String> messageMap) {
//		if (!messageMap.isEmpty()) {
//			StringBuilder message = new StringBuilder();
//
//			message.append(UPLOAD);
//			message.append(PHASE_CONTENT_SEPARATOR);
//
//			for (String s : messageMap.keySet()) {
//				Log.i("sendMessages ids", s);
//				message.append(s);
//				message.append(ID_EVENT_SEPARATOR);
//				message.append(messageMap.get(s));
//				Log.i("sendMessages messages", messageMap.get(s));
//				message.append(PAIR_SEPARATOR);
//			}
//			message.append(MESSAGE_SEPARATOR);
//			Log.i("Sending messages", message.toString());
//			BluetoothChatService.write(message.toString().getBytes());
//
//
//		} else
//			Log.i("Tried to sent empty messageMap, failed",
//					messageMap.toString());
//	}
//	private void receivedIds(Set<String> ids, String phase) {
//		if (!ids.isEmpty()) {
//			if (phase.equals(ADVERTISE)) {
//				Set<String> requestIds = new HashSet<String>();
//				for (String s : ids) {
//					if (!BluetoothChatService..containsKey(s))
//						requestIds.add(s);
//				}
//				sendIds(requestIds, REQUEST);
//
//				Map<String, String> messagesToSend = new HashMap<String, String>();
//				for (String s : events.keySet()) {
//					if (!ids.contains(s))
//						messagesToSend.put(s, events.get(s));
//				}
//				sendMessages(messagesToSend);
//			} else if (phase.equals(REQUEST)) {
//				Map<String, String> messagesToSend = new HashMap<String, String>();
//				for (String s : ids)
//					messagesToSend.put(s, events.get(s));
//
//				sendMessages(messagesToSend);
//			}
//
//		} else
//			Log.i("Got empty id set", ids.toString());
//	}
//
//	private void receivedMessages(Map<String, String> messageMap) {
//		if (!messageMap.isEmpty())
//			for (String s : messageMap.keySet()) {
//				if (!events.containsKey(s)) {
//					events.put(s, messageMap.get(s));
//					mConversationArrayAdapter.add("Received event " + s
//							+ ID_EVENT_SEPARATOR + messageMap.get(s));
//				}
//			}
//		else {
//			Log.i("Got empty id set", messageMap.toString());
//		}
//	}
//}
