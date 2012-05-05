package fi.local.social.network.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import fi.local.social.network.R;
import fi.local.social.network.db.Event;
import fi.local.social.network.db.EventImpl;
import fi.local.social.network.db.EventsDataSource;

public class NewEventActivity extends Activity {

	private EditText title = null;
	private EditText content = null;
	private Button sendBtn = null;
	private EventsDataSource eventsDataSource;

	private String sTitle;
	private String sContent;
	private long startTime;
	private long endTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newevent);
		title = (EditText) findViewById(R.id.newEventTitle);
		content = (EditText) findViewById(R.id.newEventContent);
		sendBtn = (Button) findViewById(R.id.sendBtn);
		sTitle = title.getEditableText().toString();
		sContent = content.getEditableText().toString();
		startTime = 0L; // TODO initialize with the default value
		endTime = 0L; // TODO initialize with the default value

		// open db
		eventsDataSource = new EventsDataSource(getApplicationContext());
		eventsDataSource.open();

		// When a user clicks on the 'Send Event' button, the newly created
		// event would be sent to all nearby devices
		sendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(
						getApplicationContext(),
						"The message is supposed to be sent to nearby devices.",
						Toast.LENGTH_SHORT).show();

				sTitle = title.getEditableText().toString();
				sContent = content.getEditableText().toString();

				if (sTitle.equals("")) {
					Toast.makeText(getApplicationContext(),
							"Please add a title.", Toast.LENGTH_SHORT).show();
				} else if (sContent.equals("")) {
					Toast.makeText(getApplicationContext(),
							"Please add a content description.",
							Toast.LENGTH_SHORT).show();
				} else {
					sendNewEvent();
				}
			}
		});
	}

	private void sendNewEvent() {
		Event event = new EventImpl(startTime, endTime, sTitle, sContent,
				PeopleActivity.USERNAME,null);
		eventsDataSource.createEntry(event.getDBString());

		startActivity(new Intent(getApplicationContext(), EventsActivity.class));
	}

	@Override
	protected void onPause() {
		super.onPause();
		eventsDataSource.close();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		eventsDataSource.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		eventsDataSource.open();
	}

}
