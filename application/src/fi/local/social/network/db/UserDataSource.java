package fi.local.social.network.db;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDataSource implements DataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public UserDataSource(Context context) {
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
	public List<User> getAllEntries() {

		List<User> users = new ArrayList<User>();

		String[] allColumnNames = dbHelper.getAllColumnNames(MySQLiteHelper.TABLE_USERS);
		
		
		Cursor cursor = database.query(MySQLiteHelper.TABLE_USERS,
				allColumnNames, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			User user = cursorToUser(cursor);
			users.add(user);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return users;
	}
	
	private User cursorToUser(Cursor cursor) {
		User user = new UserImpl();
		user.setID(cursor.getLong(0));

		long ts = cursor.getLong(1);
		Timestamp timestamp = new Timestamp(ts);
		user.setTime(timestamp);

		user.setUserName(cursor.getString(2));
		user.setProfilePicURI(cursor.getString(3));

		if(cursor.getInt(4) == MySQLiteHelper.isUser)
			user.setIsPhoneUser(true);
		else
			user.setIsPhoneUser(false);
		

		return user;
	}
	
	public void deleteUser(User user) 
	{
		long id = user.getID();
		System.out.println("User deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_USERS, MySQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	@Override
	public Object createEntry(String data) {
		
		ContentValues values = new ContentValues();
		
		
		int endUserName = data.indexOf(";");
		int endPicURI = data.indexOf(";", endUserName+1);
		
		String username = data.substring(0, endUserName);
		String picuri = data.substring(endUserName+1, endPicURI);
		String isPhoneU = (data.substring(endPicURI+1, data.length() ));
		int isPhoneUser = Integer.parseInt(isPhoneU);
		
		
		values.put(MySQLiteHelper.COLUMN_TIMESTAMP, System.currentTimeMillis());
		values.put(MySQLiteHelper.COLUMN_USERNAME, username);
		values.put(MySQLiteHelper.COLUMN_PICPROFILEURI, picuri);
		values.put(MySQLiteHelper.COLUMN_PHONEUSER, isPhoneUser);
		
		long insertId = database.insert(MySQLiteHelper.TABLE_USERS, null,values);
		
		String[] allColumnNames = dbHelper.getAllColumnNames(MySQLiteHelper.TABLE_USERS);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_USERS,
				allColumnNames, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		
		User user = null;
		if(cursor.moveToFirst())
			user = cursorToUser(cursor);
		cursor.close();
		return user; 	
	}

}
