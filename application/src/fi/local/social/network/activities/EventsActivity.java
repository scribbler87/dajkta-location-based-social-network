package fi.local.social.network.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.android.actionbarcompat.ActionBarActivity;

import fi.local.social.network.R;
import fi.local.social.network.db.Event;
import fi.local.social.network.db.EventsDataSource;

public class EventsActivity extends ActionBarActivity {
	private EventsDataSource eventsDataSource;
	private ArrayList<Event> events;
	private ListView listView;
	private EventItemAdapter eventItemAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_list);


		events=(ArrayList<Event>)getLastNonConfigurationInstance();
		if(events==null){
			events=new ArrayList<Event>();
			eventsDataSource = new EventsDataSource(getApplicationContext());
			eventsDataSource.open();
			events.addAll(eventsDataSource.getAllEntries());
			eventsDataSource.close();
		}

		listView=(ListView)findViewById(R.id.eventList);
		eventItemAdapter = new EventItemAdapter(this,R.layout.event_list_item,events);
		listView.setAdapter(eventItemAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				AlertDialog.Builder builder =new AlertDialog.Builder(EventsActivity.this);
				Event e=events.get(position);
				String title=e.getTitle();
				String username=e.getUser();
				String content=e.getDescription();
				
				Calendar startDate = new GregorianCalendar();
				
				AlertDialog alert = builder.create();
				alert.setTitle("Title: "+title);
				alert.setMessage("Owner: "+username+
						         "\nContent: "+content+"\nStart Time: "+e.getStartTime().toGMTString()+
						         "\nEnd Time: "+e.getEndTime().toGMTString());
				alert.setButton("OK", new DialogInterface.OnClickListener() {
				      public void onClick(DialogInterface dialog, int which) {
				 
				       //here you can add functions
				       EventsActivity.this.closeContextMenu();//finish();
				    } });
				alert.show();
			}
		});
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		events=(ArrayList<Event>)getLastNonConfigurationInstance();
		if(events==null){
			events=new ArrayList<Event>();
			eventsDataSource = new EventsDataSource(getApplicationContext());
			eventsDataSource.open();
			events.addAll(eventsDataSource.getAllEntries());
			eventsDataSource.close();
			eventItemAdapter.notifyDataSetChanged();
		}
		
	}
	
	public void onAddEventClick(View view) {
		startActivity(new Intent(getApplicationContext(), NewEventActivity.class));
	}
	
	public class EventItemAdapter extends ArrayAdapter<Event>
	{
		private ArrayList<Event> eventList;

		public EventItemAdapter(Context context, int textViewResourceId,
				List<Event> objects) 
		{
			super(context, textViewResourceId, objects);
			this.eventList=(ArrayList<Event>) objects;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View v=convertView;
			if(v==null)
			{
				LayoutInflater vi=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v=vi.inflate(R.layout.event_list_item, null);
			}
			Event event=eventList.get(position);
			if(event!=null)
			{
				TextView title=(TextView)v.findViewById(R.id.eventTitle);
				TextView content=(TextView)v.findViewById(R.id.eventContent);
				TextView username=(TextView)v.findViewById(R.id.eventUsername);
				if(title!=null){title.setText(event.getTitle());}
				if(content!=null){content.setText(event.getDescription());}
				if(username!=null){username.setText(event.getUser() + ": ");}

			}
			return v;
		}
	}
	//Save the text when screen rotation happens and restore it upon completion of the rotation.
	@Override
    public Object onRetainNonConfigurationInstance(){
		return events;
	}
}