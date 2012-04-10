package fi.local.social.network.db;

import java.sql.Timestamp;

public interface Event {

	
	public long getID();
	public String getName();
	public Timestamp getTime();
	
	
}
