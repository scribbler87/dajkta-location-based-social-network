package fi.local.social.network.db;

import java.sql.Timestamp;

public class UserImpl implements User {

	public static String createUserTable =  "create table "
			+ MySQLiteHelper.TABLE_USERS + "( " + MySQLiteHelper.COLUMN_ID
			+ " integer primary key autoincrement, " 
			+ MySQLiteHelper.COLUMN_TIMESTAMP + "   long, " 
			+ MySQLiteHelper.COLUMN_USERNAME + " text not null, " 
			+ MySQLiteHelper.COLUMN_PICPROFILEURI + " text not null, " 
			+ MySQLiteHelper.COLUMN_PHONEUSER + " int );";
	// TODO add address to db
	private String username;
	private String profilePicUri;
	private String address;
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	private long ID;

	private Timestamp time;

	private boolean isPhoneUser;
	
	public UserImpl(String uname, String uri, String address) {
		this.username = uname;
		this.profilePicUri = uri;
		this.address = address;
	}
	
	public UserImpl(){
			isPhoneUser = false;
	};
	
	@Override
	public String getUserName() {
		return username;
	}

	@Override
	public void setUserName(String uname) {
		this.username = uname;
	}

	@Override
	public String getProfilePicURI() {
		return this.profilePicUri;
	}

	@Override
	public void setProfilePicURI(String uri) {
		this.profilePicUri = uri;
	}
	
	@Override
	public String toString() {
		return this.username;
	}

	@Override
	public long getID() {
		return this.ID;
	}

	@Override
	public void setID(long ID) {
		this.ID = ID;
	}
	
	@Override
	public Timestamp getTime() {
		return this.time;
	}

	@Override
	public void setTime(Timestamp time) {
		this.time = time;
	}

	@Override
	public boolean isPhoneUser() {
		return isPhoneUser ;
	}

	@Override
	public void setIsPhoneUser(boolean b) {
		this.isPhoneUser = b;
	}
	
	public String getDBString()
	{
		String res = "";
		
		res += this.username + ";";
		res += this.profilePicUri + ";";
		if(isPhoneUser)
			res += MySQLiteHelper.isUser;
		else
			res += -MySQLiteHelper.isUser-1; // just use some user value
		
		return res;
	}
}
