package fi.local.social.network.db;

import java.sql.Timestamp;

public interface ChatMessage {

	public long getID();
	public String getText();
	public Timestamp getTime();
	public String getSenderName();
	
}
