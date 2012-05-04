package fi.local.social.network.db;

import java.sql.Timestamp;

public interface User {
	
	public long getID();
	public void setID(long ID);
	public String getUserName();
	public void setUserName(String uname);
	public String getProfilePicURI();
	public void setProfilePicURI(String uri);
	
	public String getAddress();
	public void setAddress(String address);
	
	public boolean isPhoneUser();
	public void setIsPhoneUser(boolean b);
	
	public Timestamp getTime();
	public void setTime(Timestamp time);
	
	public String getDBString();

}
