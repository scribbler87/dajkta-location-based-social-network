package fi.local.social.network.db;

import java.sql.Timestamp;

public class ChatMessageImpl implements ChatMessage{

	private long ID;
	private String text;
	private Timestamp time;
	private String senderName;
	
	
	@Override
	public long getID() {
		return ID;
	}

	@Override
	public void setID(long ID) {
		this.ID = ID;
	}

	@Override
	public String getText() {
		return this.text;
	}

	@Override
	public void setText(String text) {
		this.text = text;
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
	public String getSenderName() {
		return this.senderName;
	}

	@Override
	public void setSenderName(String name) {
		this.senderName = senderName;
	}

}
