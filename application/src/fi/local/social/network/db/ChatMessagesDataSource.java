package fi.local.social.network.db;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ChatMessagesDataSource implements DataSource{

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public ChatMessagesDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	@Override
	public void open() {
		database = dbHelper.getWritableDatabase();
	}

	@Override
	public void close() {
		dbHelper.close();
	}

	@Override
	public Object createEntry(String data) 
	{
		ContentValues values = new ContentValues();
		
		
		int endSendName = data.indexOf(";");
		int endRecName = data.indexOf(";", endSendName+1);
		
		String sender = data.substring(0, endSendName);
		String receiver = data.substring(endSendName+1, endRecName);
		String message = data.substring(endRecName+1, data.length() - 1);
		
		values.put(MySQLiteHelper.COLUMN_TIMESTAMP, System.currentTimeMillis());
		values.put(MySQLiteHelper.COLUMN_SENDERNAME, sender);
		values.put(MySQLiteHelper.COLUMN_RECEIVERNAME, receiver);
		values.put(MySQLiteHelper.COLUMN_CHATMESSAGE, message);
		long insertId = database.insert(MySQLiteHelper.TABLE_CHATMESSAGES, null,values);
		
		String[] allColumnNames = dbHelper.getAllColumnNames(MySQLiteHelper.TABLE_CHATMESSAGES);
		
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CHATMESSAGES,
				allColumnNames, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		ChatMessage newChatMessage = null;
		if(cursor.moveToFirst())
			newChatMessage = cursorToChatMessage(cursor);
		cursor.close();
		return newChatMessage; 
	}

	@Override
	public List<ChatMessage> getAllEntries() {

		List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();

		Cursor dbCursor = (Cursor) database.query(MySQLiteHelper.TABLE_CHATMESSAGES, null, null, null, null, null, null); 
		String[] columNnames = dbCursor.getColumnNames();
		
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CHATMESSAGES,
				columNnames, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ChatMessage chatMessage = cursorToChatMessage(cursor);
			chatMessages.add(chatMessage);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return chatMessages;
	}

	private ChatMessage cursorToChatMessage(Cursor cursor) {
		ChatMessage chatMessage = new ChatMessageImpl();
		chatMessage.setID(cursor.getLong(0));

		long ts = cursor.getLong(1);
		Timestamp timestamp = new Timestamp(ts);
		chatMessage.setTime(timestamp);

		chatMessage.setSenderName(cursor.getString(2));
		chatMessage.setReceiverName(cursor.getString(3));

		chatMessage.setMessage(cursor.getString(4));

		return chatMessage;
	}




}
