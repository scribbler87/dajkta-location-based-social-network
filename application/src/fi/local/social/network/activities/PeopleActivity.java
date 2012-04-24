package fi.local.social.network.activities;

import java.util.ArrayList;
import java.util.List;

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
import fi.local.social.network.R;
import fi.local.social.network.db.EventImpl;
import fi.local.social.network.db.User;
import fi.local.social.network.db.UserDataSource;
import fi.local.social.network.db.UserImpl;
import com.example.android.actionbarcompat.*;

public class PeopleActivity extends ActionBarActivity {
	
	List<User> peopleNearby;
	private UserDataSource userDatasource;
	public static String USERNAME = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.people);
		
		// TODO: get real people and their names
		// add some mockup values
		peopleNearby = new ArrayList<User>();
		peopleNearby.add(new UserImpl("Alex Yang", "add uri for pic"));
		peopleNearby.add(new UserImpl("Tom Cruise", "add uri for pic"));
		peopleNearby.add(new UserImpl("Tom Hanks", "add uri for pic"));
		peopleNearby.add(new UserImpl("Jason Stathon","add uri for pic"));
		peopleNearby.add(new UserImpl("Joe Hu", "add uri for pic"));

		ArrayAdapter<User> adapter = new ArrayAdapter<User>(this, R.layout.people_item, R.id.label, peopleNearby);
		
		ListView listView = (ListView) findViewById(R.id.mylist);
		listView.setAdapter((ListAdapter) adapter );
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
				//intent.putExtra("username", this.USERNAME);
				//Object o = listView.getItemAtPosition(position);
			    String receiverName = view.toString();
				intent.putExtra("receiver", receiverName.toString());
				startActivity(intent);
			}
		});
		
		userDatasource = new UserDataSource(this);
		userDatasource.open();
		List<User> allEntries = userDatasource.getAllEntries();
		for (User user : allEntries) {
			if(user.isPhoneUser())
			{
				this.USERNAME = user.getUserName();
				break;
			}
		}
		userDatasource.close();
		if("".equals(USERNAME))
		{
			startActivity(new Intent(getApplicationContext(), SettingActivity.class));
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		userDatasource = new UserDataSource(this);
		userDatasource.open();
		List<User> allEntries = userDatasource.getAllEntries();
		for (User user : allEntries) {
			if(user.isPhoneUser())
			{
				this.USERNAME = user.getUserName();
				break;
			}
		}
		userDatasource.close();
		if("".equals(USERNAME))
		{
			startActivity(new Intent(getApplicationContext(), SettingActivity.class));
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.menu, menu);
		return true;
	}
	
	//When a user clicks on an option menu item, start the right Activity
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		// Handle item selection
	    switch (item.getItemId()) {	     
	        case R.id.event_list:
	        	startActivity(new Intent(getApplicationContext(), EventsActivity.class));
	            return true;
	        case R.id.settings:
	        	startActivity(new Intent(getApplicationContext(), SettingActivity.class));
	        	return true;
	        case R.id.new_event:
	        	startActivity(new Intent(getApplicationContext(), NewEventActivity.class));
	        	return true;
	        default:
	        	break;
	    }
	    return false;
	}
}
