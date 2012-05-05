package fi.local.social.network.db;

import java.sql.Timestamp;

public class EventImpl implements Event {

	private long ID;
	private Timestamp timestamp;
	private Timestamp startTime;
	private Timestamp endTime;
	private String title;
	private String description;
	private String user;
	private String profilePicUri;
	
	
	public EventImpl() {
	}
	
	public EventImpl(long starttime, long endtime, String title, String description, String user, String profilePicUri) {
		this.startTime = new Timestamp(starttime);
		this.endTime = new Timestamp(endtime);
		this.title = title;
		this.description = description;
		this.user = user;
		this.profilePicUri = profilePicUri;
	}
	
	final static  String createEventsTable =  "create table "
			+ MySQLiteHelper.TABLE_EVENTS + "( " + MySQLiteHelper.COLUMN_ID
			+ " integer primary key autoincrement, " 
			+ MySQLiteHelper.COLUMN_TIMESTAMP + "   long, " 
			+ MySQLiteHelper.COLUMN_TITLE + " text not null, " 
			+ MySQLiteHelper.COLUMN_USERNAME + " text not null, " 
			+ MySQLiteHelper.COLUMN_STARTTIME + " long, " 
			+ MySQLiteHelper.COLUMN_ENDTIME + " long ,"
			+ MySQLiteHelper.COLUMN_DESCRIPTION + " text not null,"
			+ MySQLiteHelper.COLUMN_PICPROFILEURI + " text not null);"; 
	
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public long getID() {
		return this.ID;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public Timestamp getTimestamp() {
		return this.timestamp;
	}

	@Override
	public void setID(long ID) {
		this.ID = ID;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;		
	}

	@Override
	public void setTimestamp(Timestamp time) {
		this.timestamp = time;
	}

	@Override
	public String getDBString() {
		String res = "";
		
		res += title + ";";
		res += user + ";";
		res += startTime + ";";
		res += endTime + ";";
		res += description + ";";
		res += "NO_PIC"+ ";";
		
		return res;
	}
	
	@Override
	public String getProfilePicURI() {
		return this.profilePicUri;
	}

	@Override
	public void setProfilePicURI(String uri) {
		this.profilePicUri = uri;
	}

}
