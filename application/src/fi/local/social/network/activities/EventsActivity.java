package fi.local.social.network.activities;

import java.util.ArrayList;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_list);
		//ArrayList<Event> events= new ArrayList<Event>();
		//        EventItem e1=new EventItem("Party","Party at T1 at 3:46pm on April 20th");
		//        EventItem e2=new EventItem("Lecture","Lecture at T1 at 3:46pm on April 20th");
		//        EventItem e3=new EventItem("Reunion","Reunion at T1 at 3:46pm on April 20th");
		//        EventItem e4=new EventItem("Something","Party at T1 at 3:46pm on April 20th");
		//        events.add(e1);
		//        events.add(e2);
		//        events.add(e3);
		//        events.add(e4);
		//        


		events=(ArrayList<Event>)getLastNonConfigurationInstance();
		if(events==null){
			events=new ArrayList<Event>();
			eventsDataSource = new EventsDataSource(getApplicationContext());
			eventsDataSource.open();
			events.addAll(eventsDataSource.getAllEntries());
			eventsDataSource.close();
		}

		ListView listView=(ListView)findViewById(R.id.eventList);
		listView.setAdapter(new EventItemAdapter(this,R.layout.event_list_item,events));
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				/*
				TextView username=(TextView)view.findViewById(R.id.eventUsername);
				TextView title=(TextView)view.findViewById(R.id.eventTitle);
				TextView content=(TextView)view.findViewById(R.id.eventContent);
				TextView startTime=(TextView)view.findViewById(R.id.eventFrom);
				TextView endTime=(TextView)view.findViewById(R.id.eventTo);
				*/
				AlertDialog.Builder builder =new AlertDialog.Builder(EventsActivity.this);
				Event e=events.get(position);
				String title=e.getTitle();
				String username=e.getUser();
				String content=e.getDescription();
				
				/*builder.setMessage("Content: "+content.getText().toString()).setTitle("Title: "+title.getText().toString())
			       .setCancelable(false)
			       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   EventsActivity.this.finish();
			           }
			       });*/
//				AlertDialog alert = builder.create();
//				alert.setTitle("Title: "+title.getText().toString());
//				alert.setMessage("Owner: "+username.getText().toString()+
//						         "\nContent: "+content.getText().toString()+"\nStart Time: "+startTime.getText().toString()+
//						         "\nEnd Time: "+endTime.getText().toString());
//				alert.setButton("OK", new DialogInterface.OnClickListener() {
//				      public void onClick(DialogInterface dialog, int which) {
//				 
//				       //here you can add functions
//				       EventsActivity.this.closeContextMenu();//finish();
//				    } });
//				alert.show();
				
				//Toast.makeText(getApplicationContext(), username.getText().toString(), Toast.LENGTH_SHORT).show();
				//view.findViewWithTag(tag)
				
			}
			
		});
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