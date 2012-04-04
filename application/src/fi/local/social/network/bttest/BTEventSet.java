package fi.local.social.network.bttest;

import java.util.HashSet;
import java.util.Set;

public class BTEventSet extends HashSet<BTMessage> implements Messageable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3380063639332666636L;

	BTIdSet getKeySet() {
		BTIdSet result = new BTIdSet();
		for (BTMessage message : this) {
			result.add(message.getId());
		}
		return result;
	}

	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder();
		for (BTMessage m : this) {
			sb.append(m.getMessage());
			sb.append(BTActivity.MESSAGE_SEPARATOR);
		}
		return sb.toString();
	}

	public BTMessage get(BTId id) {
		for (BTMessage m : this) {
			if (m.getId().equals(id)) {
				return m;
			}
		}
		return null;
	}

	BTEventSet getFiltered(Filter<BTMessage> f) {
		BTEventSet filtered = new BTEventSet();
		for (BTMessage m : this) {
			if (!f.filtersOut(m)) {
				filtered.add(m);
			}
		}
		return filtered;
	}
	
	String buildUploadMessages() {
		StringBuilder sb = new StringBuilder();
		sb.append(BTProtocolPhase.UPLOAD + BTActivity.PHASE_CONTENT_SEPARATOR);
		sb.append(getMessage());
		return sb.toString();
	}
	
	BTIdSet buildRequestIds(BTIdSet ids) {
		BTIdSet requestIds = new BTIdSet();
		Filter<BTId> filter;
		for (BTId id : ids) {
			if (!getKeySet().contains(id))
				requestIds.add(id);
		}
		return requestIds;
	}
	
	BTEventSet buildAdvertizeMessagesToSend(BTIdSet ourIds) {
		Filter<BTMessage> filter = new IdSetEventFilter(ourIds);

		BTEventSet messagesToSend = new BTEventSet();
		for (BTId id : ourIds) {
			BTMessage m = get(id);
			if (m != null) {
				messagesToSend.add(m);
			}
		}
		return messagesToSend;
	}
}
