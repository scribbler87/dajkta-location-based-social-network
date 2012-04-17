package fi.local.social.network.db;

import java.sql.Timestamp;

public interface Event {


	
	
	public long getID();
	public void setID(long ID);
	
	public String getName();
	public void setName(String name);
	
	public Timestamp getTime();
	public void setTime(Timestamp time);
	
	public String toString();
	
	
}
