package fi.local.social.network.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import fi.local.social.network.R;
import fi.local.social.network.activities.ChatActivity.IncomingHandler;
import fi.local.social.network.btservice.BTService;
import fi.local.social.network.btservice.BTService;
import fi.local.social.network.db.EventImpl;
import fi.local.social.network.db.User;
import fi.local.social.network.db.UserDataSource;
import fi.local.social.network.db.UserImpl;
import fi.local.social.network.tools.ServiceHelper;

import com.example.android.actionbarcompat.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class PeopleActivity extends ServiceHelper {

	List<User> peopleNearby;
	private UserDataSource userDatasource;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	
	public static String USERNAME = "";
	//	final Messenger mMessenger = new Messenger(new IncomingHandler()); // Target we publish for clients to send messages to IncomingHandler.
	private ArrayAdapter<User> adapter;


	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		setContentView(R.layout.people);
		
		// Initialize Messenger
		this.mMessenger =  new Messenger(new IncomingHandler());
		
		
		// Initialize imageLoader
		ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
		ImageLoader.getInstance().init(config);

		// TODO: get real people and their names
		// add some mockup values
		peopleNearby = new ArrayList<User>();
		//		peopleNearby.add(new UserImpl("Tom Cruise", "add uri for pic"));
		//		peopleNearby.add(new UserImpl("Tom Hanks", "add uri for pic"));
		//		peopleNearby.add(new UserImpl("Jason Stathon","add uri for pic"));
		//		peopleNearby.add(new UserImpl("Joe Hu", "add uri for pic"));

		adapter = new PeopleListAdapter(this, R.layout.people_item, R.id.label, peopleNearby);
		
		ListView listView = (ListView) findViewById(R.id.mylist);
		listView.setAdapter((ListAdapter) adapter );

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(getApplicationContext(), "kjhkj", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(getApplicationContext() , ChatActivity.class);
				Bundle b = new Bundle();
				b.putString("username", USERNAME);
				b.putString("receiver", view.toString());// TODO check if this works
				String s = view.toString();
				System.err.println("viewtostring " + s);
				b.putString("address", peopleNearby.get(position).getAddress()); // TODO how to get the adress from the according user??
				intent.putExtras(b);
				startActivity(intent);



			}
		});


		// ***********check if we have a username, if not lets create one********************
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

		// ********************bind to the bluetooth service

		doBindService(PeopleActivity.this);
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		imageLoader.stop();
		try {
			System.err.println("stop service");
			//doUnbindService();
			//	stopService(new Intent(PeopleActivity.this, BTService.class));
		} catch (Throwable t) {
			Log.e("MainActivity", "Failed to unbind from the service", t);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	//	doUnRegister();
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
		doBindService(PeopleActivity.this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
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
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BTService.MSG_REC_EVENT:
				// receive a message from the bluetooth service
				String str1 = msg.getData().getString("chat");
				Toast.makeText(getApplicationContext(), str1, Toast.LENGTH_SHORT).show();
				break;

			case BTService.MSG_NEW_ADDR:
				// receive the new addr and put it into the listview
				String address = msg.getData().getString("address");
				String username = "User " + address;
				String profilePictureURI = "http://www.vugi.iki.fi/msp-api/profilePictures/"+address;
				
				UserImpl userImpl = new UserImpl(username, profilePictureURI, address);
				
				for(int i = 0; i < peopleNearby.size(); i++)
				{
					User user = peopleNearby.get(i);
					if(address.equals(user.getAddress()))
					{
						// TODO set refresh flag
						return;
					}
				}
				peopleNearby.add(userImpl);
				adapter.notifyDataSetChanged();
				Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show();
				break;
			case BTService.MSG_REGISTERED_CLIENT:
				System.err.println("startdiscovery");
				sendMessageToService("startDiscovery", "", BTService.MSG_START_DISCOVERY);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}
	
	public class PeopleListAdapter extends ArrayAdapter<User> {

		public PeopleListAdapter(Context context, int resourceId, int textViewResourceId, List<User> objects) {
			super(context, resourceId, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			User user = getItem(position);
			
			LayoutInflater inflater=getLayoutInflater();
			View row=inflater.inflate(R.layout.people_item, parent, false);
			
			TextView label=(TextView)row.findViewById(R.id.label);
			label.setText(user.getUserName());
			
			ImageView profilePicture=(ImageView)row.findViewById(R.id.profilePicture);
			imageLoader.displayImage(user.getProfilePicURI(), profilePicture);

			return row;
		}
	}
	
}
