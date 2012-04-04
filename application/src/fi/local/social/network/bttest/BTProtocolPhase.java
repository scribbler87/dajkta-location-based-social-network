package fi.local.social.network.bttest;

public enum BTProtocolPhase implements Messageable {
	ADVERTISE, REQUEST, UPLOAD;

	@Override
	public String getMessage() {
		return name();
	}

	
}
