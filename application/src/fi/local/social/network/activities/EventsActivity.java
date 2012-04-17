package fi.local.social.network.activities;

import java.util.ArrayList;
import java.util.List;

import fi.local.social.network.R;
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
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list);
        ArrayList<EventItem> events=new ArrayList<EventItem>();
        EventItem e1=new EventItem("Party","Party at T1 at 3:46pm on April 20th");
        EventItem e2=new EventItem("Lecture","Lecture at T1 at 3:46pm on April 20th");
        EventItem e3=new EventItem("Reunion","Reunion at T1 at 3:46pm on April 20th");
        EventItem e4=new EventItem("Something","Party at T1 at 3:46pm on April 20th");
        events.add(e1);
        events.add(e2);
        events.add(e3);
        events.add(e4);
        
        ListView listView=(ListView)findViewById(R.id.eventList);
        listView.setAdapter(new EventItemAdapter(this,R.layout.event_list_item,events));
    }
	public class EventItemAdapter extends ArrayAdapter<EventItem>{
		private ArrayList<EventItem> eventList;
		
		public EventItemAdapter(Context context, int textViewResourceId,
				List<EventItem> objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
			this.eventList=(ArrayList<EventItem>) objects;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			View v=convertView;
			if(v==null){
				LayoutInflater vi=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v=vi.inflate(R.layout.event_list_item, null);
			}
			EventItem event=eventList.get(position);
			if(event!=null){
				TextView title=(TextView)v.findViewById(R.id.eventTitle);
				TextView content=(TextView)v.findViewById(R.id.eventContent);
				if(title!=null){title.setText(event.title);}
				if(content!=null){content.setText(event.content);}
			}
			return v;
		}
	}
	
	public class EventItem{
		public String title;
		public String content;
		public EventItem(String title, String content){
			this.title=title;
			this.content=content;
		}
	}
}
