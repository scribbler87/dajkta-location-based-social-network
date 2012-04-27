package fi.local.social.network.bttest;

import java.util.HashSet;

import android.util.Log;

public class BTIdSet extends HashSet<BTId> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5470849284328606280L;

	public BTIdSet() {
		super();
	}
	
	public BTIdSet(String content) {
		super();
		String[] ids = content.split(BTActivity.ID_SEPARATOR);

		for (String s : ids) {
			add(new BTIdImpl(s));
		}
	}
	
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
