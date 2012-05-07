package fi.local.social.network.db;

import java.sql.Timestamp;

public class ChatMessageImpl implements ChatMessage{

	private long ID;
	private String message;
	private Timestamp time;
	private String senderName;
	private String receiverName;

	

	/* Order of the table columns!
	 * ChatMessage chatMessage = new ChatMessageImpl();
			chatMessage.setID(cursor.getLong(0));

			long ts = cursor.getLong(1);
			Timestamp timestamp = new Timestamp(ts);
			chatMessage.setTime(timestamp);

			chatMessage.setSenderName(cursor.getString(2));
			chatMessage.setReceiverName(cursor.getString(3));

			chatMessage.setText(cursor.getString(4));
	 * */
	
	final static  String createChatMessTable =  "create table "
			+ MySQLiteHelper.TABLE_CHATMESSAGES + "( " + MySQLiteHelper.COLUMN_ID
			+ " integer primary key autoincrement, " 
			+ MySQLiteHelper.COLUMN_TIMESTAMP + "   long, " 
			+ MySQLiteHelper.COLUMN_SENDERNAME + " text not null, " 
			+ MySQLiteHelper.COLUMN_RECEIVERNAME + " text not null, " 
			+ MySQLiteHelper.COLUMN_CHATMESSAGE + " text  not null );"; 


	@Override
	public long getID() {
		return ID;
	}

	@Override
	public void setID(long ID) {
		this.ID = ID;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
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
		this.senderName = name;
	}

	@Override
	public String getReceiverName() {
		return this.receiverName;
	}

	@Override
	public void setReceiverName(String name) {
		this.receiverName = name;
	}

	@Override
	public String toString() {
		String res = "";
		res += this.senderName + ": ";
		res += this.message;
		return res;
	};

	
	public String getDBString()
	{
		String res = "";
		
		res += this.senderName + ";";
		res += this.receiverName + ";";
		res += this.message; // just use some user value
		
		return res;
	}

	@Override
	public int compareTo(ChatMessage cm) {
	        if (this.getTime().getTime() < cm.getTime().getTime())
	            return -1;
	        else if (this.getTime().getTime() == cm.getTime().getTime())
	            return 0;
	        else
	            return 1;
	}
	
}
