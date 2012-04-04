package fi.local.social.network.bttest;

public class BTContentImpl implements BTContent {

	private String content;

	public BTContentImpl(String content) {
		this.content = content;
	}

	@Override
	public String getMessage() {
		return content;
	}

}
