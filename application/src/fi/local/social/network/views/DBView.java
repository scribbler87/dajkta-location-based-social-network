package fi.local.social.network.views;

import java.util.List;
import java.util.Random;

import fi.local.social.network.R;
import fi.local.social.network.db.Comment;
import fi.local.social.network.db.CommentsDataSource;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;


public class DBView extends ListActivity  {
	private CommentsDataSource datasource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dbview);
		
		//Button goBackButton = (Button) findViewById(R.id.buttonDBgoBack);
		//goBackButton.setOnClickListener(this);
		
		datasource = new CommentsDataSource(this);
		datasource.open();

		List<Comment> values = datasource.getAllComments();

		// Use the SimpleCursorAdapter to show the
		// elements in a ListView
		ArrayAdapter<Comment> adapter = new ArrayAdapter<Comment>(this,
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
	}

	// Will be called via the onClick attribute
		// of the buttons in main.xml
		public void onClick(View view) {
			@SuppressWarnings("unchecked")
			ArrayAdapter<Comment> adapter = (ArrayAdapter<Comment>) getListAdapter();
			Comment comment = null;
			switch (view.getId()) {
			case R.id.buttonDBbTestConf1:
				String[] comments = new String[] { "Cool", "Very nice", "Hate it" };
				int nextInt = new Random().nextInt(3);
				// Save the new comment to the database
				comment = datasource.createComment(comments[nextInt]);
				adapter.add(comment);
				break;
			}
//			case R.id.delete:
//				if (getListAdapter().getCount() > 0) {
//					comment = (Comment) getListAdapter().getItem(0);
//					datasource.deleteComment(comment);
//					adapter.remove(comment);
//				}
//				break;
//			}
//			adapter.notifyDataSetChanged();
		}
	
//	@Override
//	public void onClick(View v) {
//		super.onBackPressed();
//	}
	
	@Override
	protected void onResume() {
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}
}
