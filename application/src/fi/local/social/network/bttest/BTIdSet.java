package fi.local.social.network.bttest;

import java.util.HashSet;

public class BTIdSet extends HashSet<BTId> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5470849284328606280L;

	String buildIdString(BTProtocolPhase phase) {
		StringBuilder idStringBuilder = new StringBuilder();

		idStringBuilder.append(phase.getMessage());
		idStringBuilder.append(BTActivity.PHASE_CONTENT_SEPARATOR);

		for (BTId id : this) {
			idStringBuilder.append(id.getMessage());
			idStringBuilder.append(BTActivity.ID_SEPARATOR);
		}
		idStringBuilder.append(BTActivity.MESSAGE_SEPARATOR);
		String idString = idStringBuilder.toString();
		return idString;
	}
}
