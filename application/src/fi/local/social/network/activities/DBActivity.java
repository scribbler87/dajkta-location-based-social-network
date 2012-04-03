package fi.local.social.network.activities;

import java.util.List;

import fi.local.social.network.R;
import fi.local.social.network.db.Comment;
import fi.local.social.network.db.CommentsDataSource;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
/**
 * Simulates a first way to use the android sqlite db
 * 
 * 
 * @author jens
 *
 */

public class DBActivity extends ListActivity  implements OnClickListener{
	private CommentsDataSource datasource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dbtest);
		
		Button goBackButton = (Button) findViewById(R.id.buttonDBgoBack);
		
		goBackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DBActivity.super.onBackPressed();
			}
		});
		
		Button button = (Button) findViewById(R.id.buttonDBbConfirm);
		button.setOnClickListener(this);
		
		button = (Button) findViewById(R.id.buttonDBDelete);
		button.setOnClickListener(this);
		
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
			case R.id.buttonDBbConfirm:
				EditText edText= (EditText) findViewById(R.id.textDB1);
				
				String editAbleText = edText.getText().toString();
				if(editAbleText.length() == 0)
				{
					AlertDialog alertDialog = new AlertDialog.Builder(this).create();
					alertDialog.setTitle("Empty Text field");
					alertDialog.setMessage("The text field is empty");
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					      public void onClick(DialogInterface dialog, int which) {
					    	  return;
					    } });
					alertDialog.show();
				}
				else
				{
					// Save the new comment to the database
					comment = datasource.createComment(editAbleText);
					adapter.add(comment);
				}
				
				
				break;
				
			case R.id.buttonDBDelete:
				
				edText = (EditText) findViewById(R.id.textDB2);
				
				editAbleText = edText.getText().toString();
				if(editAbleText.length() == 0)
				{
					AlertDialog alertDialog = new AlertDialog.Builder(this).create();
					alertDialog.setTitle("Empty Text field");
					alertDialog.setMessage("The text field is empty. Please fill in with the entry you want to delete.");
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					      public void onClick(DialogInterface dialog, int which) {
					    	  return;
					    } });
					alertDialog.show();
				}
				else
				{
					// Save the new comment to the database
					List<Comment> allComments = datasource.getAllComments();
					int counter = 0;
					for (Comment actComment : allComments) 
					{
						if (actComment.getComment().equals(editAbleText))
						{
							datasource.deleteComment(actComment);
							counter++;
						}
					}
					
					AlertDialog alertDialog = new AlertDialog.Builder(this).create();
					alertDialog.setTitle("deleted db entries");
					alertDialog.setMessage("You have deleted " + counter + " entries in the db.");
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					      public void onClick(DialogInterface dialog, int which) {
					    	  return;
					    } });
					alertDialog.show();
					
					
				}
				
				break;
			}
			
			
			adapter.notifyDataSetChanged();
		}
	
	
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
