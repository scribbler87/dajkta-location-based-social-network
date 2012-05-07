package fi.local.social.network.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.actionbarcompat.ActionBarActivity;

import fi.local.social.network.R;
import fi.local.social.network.db.Event;
import fi.local.social.network.db.EventImpl;
import fi.local.social.network.db.EventsDataSource;

public class NewEventActivity extends ActionBarActivity {

	private static final int SELECT_PICTURE = 1;
	private TextView eventStartTimeValue,eventEndTimeValue;
	private EditText title=null;
	private EditText content=null;
	private ImageView image=null;
	private Button chooseImageBtn=null;
	private Button sendBtn=null;
	private Bitmap bitmap=null;
	private EventsDataSource eventsDataSource;
	private Uri selectedImage;
	private String sTitle;
	private String sContent;
	private String sUri;
	private long startTime;
	private long endTime;
	private final int START_DATE_DIALOG_ID=100;
	private final int END_DATE_DIALOG_ID=101;
	private final int START_TIME_PICKER_ID=200;
	private final int END_TIME_PICKER_ID=201;
	private int year, month, day, hour, minute, endYear, endMonth, endDay;
	
	private TimePickerDialog.OnTimeSetListener timePickerListenerStart=
		new TimePickerDialog.OnTimeSetListener() {
			
			@Override
			public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
				// TODO Auto-generated method stub
				hour=selectedHour;
				minute=selectedMinute;
				eventStartTimeValue.setText(eventStartTimeValue.getText().toString()+" "+
						String.valueOf(hour)+":"+String.valueOf(minute));
				
			}
		};

	private TimePickerDialog.OnTimeSetListener timePickerListenerEnd=
		new TimePickerDialog.OnTimeSetListener() {
			
			@Override
			public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
				// TODO Auto-generated method stub
				hour=selectedHour;
				minute=selectedMinute;
				eventEndTimeValue.setText(eventEndTimeValue.getText().toString()+" "+
						String.valueOf(hour)+":"+String.valueOf(minute));
				
			}
		};
			
	private DatePickerDialog.OnDateSetListener datePickerListenerStart=
		new DatePickerDialog.OnDateSetListener() {
		
		//When dialog is closed, method below gets called
		@Override
		public void onDateSet(DatePicker view, int selectedYear, int selectedMonth,int selectedDay) {
			// TODO Auto-generated method stub
			year=selectedYear;
			month=selectedMonth;
			day=selectedDay;
			eventStartTimeValue.setText(""+(month+1)+"-"+day+"-"+year);
		
		}
	};

	private DatePickerDialog.OnDateSetListener datePickerListenerEnd=
		new DatePickerDialog.OnDateSetListener() {
		
		//When dialog is closed, method below gets called
		@Override
		public void onDateSet(DatePicker view, int selectedYear, int selectedMonth,
				int selectedDay) {
			// TODO Auto-generated method stub
			endYear=selectedYear;
			endMonth=selectedMonth;
			endDay=selectedDay;
			eventEndTimeValue.setText(""+(endMonth+1)+"-"+endDay+"-"+endYear);
		
		}
	};
	private EventsDataSource eventsDataSource1;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newevent);

		eventEndTimeValue=(TextView)findViewById(R.id.eventEndTimeValue);
		eventStartTimeValue=(TextView)findViewById(R.id.eventStartTimeValue);
		title=(EditText)findViewById(R.id.newEventTitle);
		content=(EditText)findViewById(R.id.newEventContent);
		
		//Restore data when screen rotation happens
		if(DataToRetain.title!=null)
			title.setText(DataToRetain.title);
		if(DataToRetain.content!=null)
			content.setText(DataToRetain.content);
		if(DataToRetain.startTime!=null)
			eventStartTimeValue.setText(DataToRetain.startTime);
		if(DataToRetain.endTime!=null)
			eventEndTimeValue.setText(DataToRetain.endTime);
		
		
		image=(ImageView)findViewById(R.id.imgView);
		//chooseImageBtn=(Button)findViewById(R.id.chooseImageBtn);
		sendBtn=(Button)findViewById(R.id.sendBtn);
		selectedImage = null;
		sTitle = title.getEditableText().toString();
		sContent = content.getEditableText().toString();
		sUri = "";
		startTime = 0L; // TODO initialize with the default value
		endTime = 0L; // TODO initialize with the default value

		// open db
		eventsDataSource = new EventsDataSource(getApplicationContext());
		eventsDataSource.open();


		Bitmap bitmapTemp=(Bitmap)getLastNonConfigurationInstance();
		if(bitmapTemp!=null){
			image.setImageBitmap(bitmapTemp);
			bitmap=bitmapTemp;
		}

		//When a user clicks on the 'choose a image' button, the user is directed into the media folder to choose a image
		/*
		chooseImageBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//Launch an image choosing activity
				Intent imageChoosingIntent = new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(imageChoosingIntent, SELECT_PICTURE);
			}
		});
		*/

		
		Button sendBtn = (Button) findViewById(R.id.sendBtn);

		// open db
		eventsDataSource = new EventsDataSource(getApplicationContext());
		

		// When a user clicks on the 'Send Event' button, the newly created
		// event would be sent to all nearby devices
		sendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(
						getApplicationContext(),
						"The message is supposed to be sent to nearby devices.",
						Toast.LENGTH_SHORT).show();


				EditText title = (EditText) findViewById(R.id.newEventTitle);
				String sTitle = title.getEditableText().toString();
				EditText content = (EditText) findViewById(R.id.newEventContent);
				String sContent = content.getEditableText().toString();
				
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
					sendNewEvent(sTitle, sContent);
				}
			}
		});
	}
	
	//When a user clicks on the label "date" besides "Start from", date dialog is initiated
	public void chooseStartTime(View v){
		showDialog(START_DATE_DIALOG_ID);
	}
	//When a user clicks on the label "date" besides "Last Until:", date dialog is initiated
	public void chooseEndTime(View v){
		showDialog(END_DATE_DIALOG_ID);
	}
	//When a user clicks on the label "time" besides "Start from", date dialog is initiated
	public void chooseStartTimeHour(View v){
		showDialog(START_TIME_PICKER_ID);
	}
	//When a user clicks on the label "time" besides "Last Until:", time picker dialog is initiated
	public void chooseEndTimeHour(View v){
		showDialog(END_TIME_PICKER_ID);
	}
	@Override
	protected Dialog onCreateDialog(int id){
		switch(id){
		case START_DATE_DIALOG_ID:
			return new DatePickerDialog(this, datePickerListenerStart, year, month, day);
		case END_DATE_DIALOG_ID:
			return new DatePickerDialog(this, datePickerListenerEnd, year, month, day);
		case START_TIME_PICKER_ID:
			return new TimePickerDialog(this, timePickerListenerStart, hour, minute,true);
		case END_TIME_PICKER_ID:
			return new TimePickerDialog(this, timePickerListenerEnd, hour, minute,true);
		}
		return null;
	}
	
	private void sendNewEvent() 
	{
		Event event = new EventImpl(startTime, endTime, sTitle, sContent, PeopleActivity.USERNAME, sUri);
	}

	private void sendNewEvent(String sTitle, String sContent) {
		Event event = new EventImpl(0L, 0L, sTitle, sContent,
				PeopleActivity.USERNAME, null);
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
	}

	@Override
	protected void onResume() {
		super.onResume();
		eventsDataSource.open();
	}
	//Save the text when screen rotation happens and restore it upon completion of the rotation.
	@Override
    public Object onRetainNonConfigurationInstance(){
    	DataToRetain.title=title.getText().toString();
    	DataToRetain.content=content.getText().toString();
    	DataToRetain.startTime=eventStartTimeValue.getText().toString();
    	DataToRetain.endTime=eventEndTimeValue.getText().toString();
		return null;
    }
}
