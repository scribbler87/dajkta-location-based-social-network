package fi.local.social.network.activities;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import fi.local.social.network.R;

public class PeopleActivity extends ListActivity {
	/*static final String[] PEOPLE_NEAR=new String[]{
		   "Alex Yang","Tom Cruise","Tom Hanks","Jason Stathon","Joe Hu",
		   "Alex Yang","Tom Cruise","Tom Hanks","Jason Stathon","Joe Hu",
		   "Alex Yang","Tom Cruise","Tom Hanks","Jason Stathon","Joe Hu",
		   "Alex Yang","Tom Cruise","Tom Hanks","Jason Stathon","Joe Hu"};*/
	ArrayList<String> PEOPLE_NEAR=new ArrayList<String>();
	
			@Override
			public void onCreate(Bundle savedInstanceState){
				super.onCreate(savedInstanceState);
				PEOPLE_NEAR.add("Jason Stathon");PEOPLE_NEAR.add("Tom Hanks");PEOPLE_NEAR.add("Jason Stathon");
				PEOPLE_NEAR.add("Alex Yang");PEOPLE_NEAR.add("Alex Yang");PEOPLE_NEAR.add("Tom Hanks");
				PEOPLE_NEAR.add("Alex Yang");PEOPLE_NEAR.add("Jason Stathon");PEOPLE_NEAR.add("Alex Yang");
				
				String[] PEOPLE_NEARBY=PEOPLE_NEAR.toArray(new String[1]);
				setListAdapter((ListAdapter) new ArrayAdapter<String>(this, R.layout.peoplenearby,PEOPLE_NEARBY));
				ListView listView = getListView();
				listView.setTextFilterEnabled(true);
				
				listView.setOnItemClickListener(new OnItemClickListener() {
					//When a name is clicked, a notification pops up with the name
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
						Toast.makeText(getApplicationContext(),
						((TextView) view).getText(), Toast.LENGTH_SHORT).show();
						}
					});
			}//end onCreate
			
			@Override
			public boolean onCreateOptionsMenu(Menu menu){
				MenuInflater inflater=getMenuInflater();
				inflater.inflate(R.layout.menu_people_nearby, menu);
				return true;
			}
			
			//When a user clicks on an option menu item, a toast with the item title shows up
			
			@Override
			public boolean onOptionsItemSelected(MenuItem item) {
				super.onOptionsItemSelected(item);
				// Handle item selection
			    switch (item.getItemId()) {
			        case R.id.friends:
			            Toast.makeText(getApplicationContext(),"You choose option menu item: "+item.getTitle(), Toast.LENGTH_SHORT).show();
			            return true;
			        case R.id.event_list:
			        	//Toast.makeText(getApplicationContext(),"You choose option menu item: "+item.getTitle(), Toast.LENGTH_SHORT).show();
			        	startActivity(new Intent(getApplicationContext(), EventsActivity.class));
			            return true;
			        case R.id.groups:
			        	Toast.makeText(getApplicationContext(),"You choose option menu item: "+item.getTitle(), Toast.LENGTH_SHORT).show();
			        	return true;
			        case R.id.settings:
			        	//Toast.makeText(getApplicationContext(),"You choose option menu item: "+item.getTitle(), Toast.LENGTH_SHORT).show();
			        	startActivity(new Intent(getApplicationContext(), SettingActivity.class));
			        	return true;
			        case R.id.new_event:
			        	startActivity(new Intent(getApplicationContext(), NewEventActivity.class));
			        	//Toast.makeText(getApplicationContext(),"You choose option menu item: "+item.getTitle(), Toast.LENGTH_SHORT).show();
			        	return true;
			        default:
			        	break;
			    }
			    return false;
			}
}
