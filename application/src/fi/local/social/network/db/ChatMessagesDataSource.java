package fi.local.social.network.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ChatMessagesDataSource implements DataSource{

	// Database fields
		private SQLiteDatabase database;
		private MySQLiteHelper dbHelper;
		private String[] allColumns = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_COMMENT };

		public ChatMessagesDataSource(Context context, String DATABASE_NAME) {
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
		public CommentImpl createEntry(String comment) {
			ContentValues values = new ContentValues();
			values.put(MySQLiteHelper.COLUMN_COMMENT, comment);
			long insertId = database.insert(MySQLiteHelper.TABLE_COMMENTS, null,
					values);
			Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
					allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
					null, null, null);
			cursor.moveToFirst();
			CommentImpl newComment = null; //cursorToEntry(cursor);
			cursor.close();
			return newComment;
		}

		public void deleteEntry(CommentImpl comment) {
			long id = comment.getId();
			System.out.println("Comment deleted with id: " + id);
			database.delete(MySQLiteHelper.TABLE_COMMENTS, MySQLiteHelper.COLUMN_ID
					+ " = " + id, null);
		}

		@Override
		public List<CommentImpl> getAllEntries() {
			List<CommentImpl> comments = new ArrayList<CommentImpl>();

			Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
					allColumns, null, null, null, null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				CommentImpl comment =null;// cursorToEntry(cursor);
				comments.add(comment);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
			return comments;
		}
		
		
		private ChatMessageImpl cursorToEntry(Cursor cursor) {
			ChatMessageImpl chatMessage = new ChatMessageImpl();
			chatMessage.setID(cursor.getLong(0));
			//schatMessage.setText((cursor.getString(1));
			return chatMessage;
		}

		



}
