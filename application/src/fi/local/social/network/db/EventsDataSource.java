package fi.local.social.network.db;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class EventsDataSource implements DataSource{

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_COMMENT };

	public EventsDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	@Override
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	@Override
	public void close() {
		dbHelper.close();
	}

	@Override
	public Object createEntry(String entry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getAllEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	private Event cursorToEntry(Cursor cursor) {
		EventImpl event = new EventImpl();
		// TODO
		return event;
	}


}
