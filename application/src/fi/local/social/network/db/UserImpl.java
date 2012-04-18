package fi.local.social.network.db;

public class UserImpl implements User {

	private String username;
	private String profilePicUri;
	
	public UserImpl(String uname, String uri) {
		this.username = uname;
		this.profilePicUri = uri;
	}
	
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

}
