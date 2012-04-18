package fi.local.social.network.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_COMMENTS = "comments";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_COMMENT = "comment";

	private static final String DATABASE_NAME = "mobileNeighbour.db";
	private static final int DATABASE_VERSION = 1;
	
	
	// chat message specific columns and other constants
	public static final String TABLE_CHATMESSAGES = "chatmessages";
	
	public static final String COLUMN_CHATMESSAGE = "chatmessage";
	public static final String COLUMN_TIMESTAMP = "timestamp";
	public static final String COLUMN_SENDERNAME = "sendername";
	public static final String COLUMN_RECEIVERNAME = "receivername";
	
	
	
	// TODO add the other tables as well
	

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) 
	{
		// create the database
		final String dbCreate = getCreateDB();
		database.execSQL(dbCreate);
	}

	private static String getCreateDB() 
	{
		String res = "";
		
		res += ChatMessageImpl.createChatMessTable;
		
		return res;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
		onCreate(db);
	}
	
	public final String[] getAllColumnNames(String tableName)
	{
		Cursor dbCursor = (Cursor) this.getWritableDatabase().query(tableName, null, null, null, null, null, null); 
		String[] columNames = dbCursor.getColumnNames();
		
		return columNames;
	}

}
