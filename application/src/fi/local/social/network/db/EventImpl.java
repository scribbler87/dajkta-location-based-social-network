package fi.local.social.network.db;

import java.sql.Timestamp;

public class EventImpl implements Event {

	private long ID;
	private String name;
	private Timestamp time;
	
	
	@Override
	public long getID() {
		return this.ID;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Timestamp getTime() {
		return this.time;
	}

	@Override
	public void setID(long ID) {
		this.ID = ID;
	}

	@Override
	public void setName(String name) {
		this.name = name;		
	}

	@Override
	public void setTime(Timestamp time) {
		this.time = time;
	}

}
