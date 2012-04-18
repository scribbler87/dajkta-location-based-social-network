package fi.local.social.network.db;

import java.sql.Timestamp;

public interface ChatMessage {

	public long getID();
	public void setID(long ID);
	
	public String getMessage();
	public void setMessage(String message);
	
	public Timestamp getTime();
	public void setTime(Timestamp time);
	
	public String getSenderName();
	public void setSenderName(String name);
	
	public String getReceiverName();
	public void setReceiverName(String name);
	
	public String toString();
	
	public String getDBString();
	
	
}
