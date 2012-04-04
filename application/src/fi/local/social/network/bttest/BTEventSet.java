package fi.local.social.network.bttest;

import java.util.HashSet;
import java.util.Set;

public class BTEventSet extends HashSet<BTMessage> implements Messageable {
	Set<BTId> getKeySet() {
		Set<BTId> result = new HashSet<BTId>();
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

	public BTEventSet getFiltered(Filter f) {
		BTEventSet filtered = new BTEventSet();
		for (BTMessage m : this) {
			if (!f.filtersOut(m)) {
				filtered.add(m);
			}
		}
		return filtered;
	}
}
