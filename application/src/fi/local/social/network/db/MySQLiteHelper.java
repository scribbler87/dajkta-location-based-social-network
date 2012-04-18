package fi.local.social.network.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	

	private static final String DATABASE_NAME = "mobileNeighbour.db";
	private static final int DATABASE_VERSION = 4;
	
	
	public static int isUser = 1;
	
	// generall column names
	public static final String COLUMN_TIMESTAMP = "timestamp";
	public static final String COLUMN_ID = "_id";
	
	// chat message specific columns and other constants
	public static final String COLUMN_CHATMESSAGE = "chatmessage";
	public static final String COLUMN_SENDERNAME = "sendername";
	public static final String COLUMN_RECEIVERNAME = "receivername";
	
	// user specific data
	public static final String COLUMN_USERNAME = "chatmessage";
	public static final String COLUMN_PICPROFILEURI = "picuri";
	public static final String COLUMN_PHONEUSER = "phoneuser"; // to show if this is the username on "THIS" phone
	
	
	
	// TODO add the other tables as well
	public static final String TABLE_CHATMESSAGES = "chatmessages";
	public static final String TABLE_USERS = "users";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) 
	{
		// create the database
		database.execSQL(ChatMessageImpl.createChatMessTable);
		database.execSQL(UserImpl.createUserTable);
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHATMESSAGES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
		onCreate(db);
	}
	
	public final String[] getAllColumnNames(String tableName)
	{
		Cursor dbCursor = (Cursor) this.getWritableDatabase().query(tableName, null, null, null, null, null, null); 
		
		String[] columNames = dbCursor.getColumnNames();
		
		return columNames;
	}

}
