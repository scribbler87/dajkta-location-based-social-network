package fi.local.social.network.bttest;

import java.io.Serializable;

public interface BTMessage extends Messageable,Serializable{

	public BTId getId();
	public String getContent();
	public String getMessage();
}
