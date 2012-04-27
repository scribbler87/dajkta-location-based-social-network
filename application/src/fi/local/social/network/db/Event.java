package fi.local.social.network.db;

import java.sql.Timestamp;

public interface Event {


	
	
	public long getID();
	public void setID(long ID);
	
	public String getTitle();
	public void setTitle(String title);
	
	public String getDescription();
	public void setDescription(String desc);
	
	public Timestamp getTimestamp();
	public void setTimestamp(Timestamp time);
	
	public Timestamp getStartTime();
	public void setStartTime(Timestamp time);
	
	public Timestamp getEndTime();
	public void setEndTime(Timestamp time);
	
	public String getUser();
	public void setUser(String user);
	
	public String getProfilePicURI();
	public void setProfilePicURI(String uri);
	
	public String toString();
	
	public String getDBString();
	
	
}
