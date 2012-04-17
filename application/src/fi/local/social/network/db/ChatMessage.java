package fi.local.social.network.db;

import java.sql.Timestamp;

public interface ChatMessage {

	public long getID();
	public void setID(long ID);
	
	public String getText();
	public void setText(String text);
	
	public Timestamp getTime();
	public void setTime(Timestamp time);
	
	public String getSenderName();
	public void setSenderName(String name);
	
	public String toString();
	
}
