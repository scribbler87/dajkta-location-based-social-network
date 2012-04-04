package fi.local.social.network.bttest;

public interface BTMessage extends Messageable{

	public BTId getId();
	public String getContent();
	public String getMessage();
}
