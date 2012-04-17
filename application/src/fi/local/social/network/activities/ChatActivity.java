package fi.local.social.network.activities;

import java.util.ArrayList;
import java.util.List;

import fi.local.social.network.R;
import fi.local.social.network.db.Comment;
import fi.local.social.network.db.CommentImpl;
import fi.local.social.network.db.CommentsDataSource;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;


public class ChatActivity  extends Activity {
	
	private Button sendButton;
	private EditText edittext;
	private ListView chatHist;
	private List chatList;
	private CommentsDataSource datasource;
	private Comment comment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);

		sendButton = (Button) findViewById(R.id.buttonChatConfirm);
		edittext = (EditText) findViewById(R.id.edit_text_out);
		chatHist = (ListView) findViewById(R.id.listChat);
		chatList = new ArrayList<String>();
		
		
		
		new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				// TODO Auto-generated method stub
				
			}
		};
		
		// TODO: get unique table name from both users
		String tableID = "username1" + "_username_2";
		
		
		datasource = new CommentsDataSource(this,tableID);
		datasource.open();

		List<CommentImpl> allComments = datasource.getAllEntries();
		if(allComments.size() > 0)
			chatList.addAll(allComments);
		
		
		
		ListAdapter adapter = new ArrayAdapter<List>(getApplicationContext(), android.R.layout.simple_list_item_1, chatList);
		chatHist.setAdapter(adapter);
		
		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				 // Send a message using content of the edit text widget
                String message = edittext.getText().toString();
                sendMessage(message);
			}
		});
		
		
		edittext.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent event) {
				  // If the event is a key-down event on the "enter" button
		        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
		            (keyCode == KeyEvent.KEYCODE_ENTER)) {
		        	String message = edittext.getText().toString();
	                sendMessage(message);
	                return true;
		        }
		        
		        return false;
			}
		});
	}
	


	private void sendMessage(String message)
	{
		// TODO: Check that we're actually connected before trying anything
    

        // Check that there's actually something to send
        if (message.length() > 0) {
        	System.err.println(message);
        	
        	// TODO: add username
        	String userName = "user";
        	message = userName + ": "+ message;
        	
        
        	// Save the new comment to the database
			comment = datasource.createEntry(message);

        	
        	chatList.add(message);
        	edittext.setText("");
        	
        	
        	Intent intent = new Intent();
        	intent.putExtra("chatmessage", message);
        	
        	sendBroadcast(intent);
        }
	}
	

}
