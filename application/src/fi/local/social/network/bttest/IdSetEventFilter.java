package fi.local.social.network.bttest;

import java.util.Set;

class IdSetEventFilter implements Filter<BTMessage> {

	private Set<BTId> ids;
	
	public IdSetEventFilter(Set<BTId> ids) {
		this.ids = ids;
	}
	
	@Override
	public boolean filtersOut(BTMessage m) {
		return !ids.contains(m.getId());
	}
	
}