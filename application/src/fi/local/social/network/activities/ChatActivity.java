package fi.local.social.network.activities;

import fi.local.social.network.R;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ChatActivity  extends ListActivity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		
	//	ListView mConversationView = (ListView) findViewById(R.id.);
		
		Button sendButton = (Button) findViewById(R.id.buttonChatConfirm);
		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				 // Send a message using content of the edit text widget
                TextView view = (TextView) findViewById(R.id.edit_text_out);
                String message = view.getText().toString();
                sendMessage(message);
			}

			
		});
	}
	
	private void sendMessage(String message)
	{
		// Check that we're actually connected before trying anything
    

        // Check that there's actually something to send
        if (message.length() > 0) {
        	
        }
		
	}

}
