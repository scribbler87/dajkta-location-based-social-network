package fi.local.social.network.activities;

import java.util.ArrayList;
import java.util.List;

import fi.local.social.network.R;
import fi.local.social.network.db.ChatMessage;
import fi.local.social.network.db.ChatMessagesDataSource;
import fi.local.social.network.db.Event;
import fi.local.social.network.db.EventsDataSource;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class EventsActivity extends Activity {
	private EventsDataSource eventsDataSource;
	private ListView lvEventsHist;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_list);
		ArrayList<Event> events= new ArrayList<Event>();
		//        EventItem e1=new EventItem("Party","Party at T1 at 3:46pm on April 20th");
		//        EventItem e2=new EventItem("Lecture","Lecture at T1 at 3:46pm on April 20th");
		//        EventItem e3=new EventItem("Reunion","Reunion at T1 at 3:46pm on April 20th");
		//        EventItem e4=new EventItem("Something","Party at T1 at 3:46pm on April 20th");
		//        events.add(e1);
		//        events.add(e2);
		//        events.add(e3);
		//        events.add(e4);
		//        



		eventsDataSource = new EventsDataSource(getApplicationContext());
		eventsDataSource.open();
		events.addAll(eventsDataSource.getAllEntries());
		eventsDataSource.close();


		ListView listView=(ListView)findViewById(R.id.eventList);
		listView.setAdapter(new EventItemAdapter(this,R.layout.event_list_item,events));
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
}