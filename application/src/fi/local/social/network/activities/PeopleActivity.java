package fi.local.social.network.activities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import fi.local.social.network.R;
import fi.local.social.network.btservice.BTService;
import fi.local.social.network.db.User;
import fi.local.social.network.db.UserDataSource;
import fi.local.social.network.db.UserImpl;
import fi.local.social.network.tools.ServiceHelper;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class PeopleActivity extends ServiceHelper {

	private static String APIPATH= "http://www.vugi.iki.fi/msp-api/getName.php?address=";
	private static String PICTUREPATH= "http://www.vugi.iki.fi/msp-api/profilePictures/";
	
	List<User> peopleNearby;
	private UserDataSource userDatasource;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	
	public static String USERNAME = "";
	private static String ADDRESS = "";
	//	final Messenger mMessenger = new Messenger(new IncomingHandler()); // Target we publish for clients to send messages to IncomingHandler.
	private ArrayAdapter<User> adapter;
	private DisplayImageOptions options;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		setContentView(R.layout.people);
		
		// Initialize Messenger
		this.mMessenger =  new Messenger(new IncomingHandler());
		
		// Initialize imageLoader
		ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
		ImageLoader.getInstance().init(config);
		
		// Initialize options for loaded images 
		options = new DisplayImageOptions.Builder()
			.showImageForEmptyUrl(R.drawable.profile_default)
			.showStubImage(R.drawable.profile_default)
			.cacheInMemory()
			.cacheOnDisc()
			.build();
		
		// Initialize people nearby -list
		peopleNearby = new ArrayList<User>();
		
	
		
		// Create list adapter
		adapter = new PeopleListAdapter(this, R.layout.people_item, R.id.label, peopleNearby);
		
		GridView gridView = (GridView)findViewById(R.id.mylist);
		gridView.setAdapter((ListAdapter) adapter );

		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				User user = peopleNearby.get(position);
				ADDRESS = user.getAddress();
				Bundle b = new Bundle();
				b.putString("address", ADDRESS);
				sendBundleToService(b,  BTService.MSG_START_CONNCETION);
			}
		});
		
		gridView.setEmptyView(findViewById(R.id.empty));


		// ***********check if we have a username, if not lets create one********************
		userDatasource = new UserDataSource(this);
		userDatasource.open();
		List<User> allEntries = userDatasource.getAllEntries();
		for (User user : allEntries) {
			if(user.isPhoneUser())
			{
				USERNAME = user.getUserName();
				break;
			}
		}
		userDatasource.close();
		if("".equals(USERNAME))
		{
			startActivity(new Intent(getApplicationContext(), SettingActivity.class));
		}

		// ******************** bind to the bluetooth service
		doBindService(PeopleActivity.this);
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		imageLoader.stop();
		stopService(getIntent());
	}

	@Override
	protected void onPause() {
		super.onPause();
	}	

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
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
		
		//doBindService(PeopleActivity.this);
		this.startDiscovery();
	}
	
	public void startDiscovery(){
		sendMessageToService("startDiscovery", "", BTService.MSG_START_DISCOVERY);
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
		case R.id.menu_refresh:	
			this.startDiscovery();
            Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
            
            // Show spinner for a while..
            getActionBarHelper().setRefreshActionItemState(true);
            getWindow().getDecorView().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            getActionBarHelper().setRefreshActionItemState(false);
                        }
                    }, 10000);
            break;
		default:
			break;
		}
		return false;
	}
	
	class HTTPNameRequest extends AsyncTask<String, String, String>{
		
		private String address;
		private String TAG = "HTTPNameRequest";

	    @Override
	    protected String doInBackground(String... btAddress) {
	    	
	    	this.address = btAddress[0];
	    	Log.d(TAG,"Starting request for address: " + address);
	    	
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpResponse response;
	        String responseString = null;
	        try {
	            response = httpclient.execute(new HttpGet(PeopleActivity.APIPATH + address));
	            StatusLine statusLine = response.getStatusLine();
	            
	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                responseString = out.toString();
	            } else{
	                response.getEntity().getContent().close();
	                throw new IOException(statusLine.getReasonPhrase());
	            }
	        } catch (IOException e) {
	        	Log.e(TAG,"Got IOException");
	            //TODO Handle problems..
	        }
	        return responseString;
	    }

	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        if (result != null){
	        	Log.d(TAG,"Result for adress " + address + ": " + result);
	        	for(int i = 0; i < peopleNearby.size(); i++) {
					User user = peopleNearby.get(i);
					if(this.address.equals(user.getAddress())){
						Log.d(TAG,"Setting user name for: " + user.getAddress());
						user.setUserName(result);
						adapter.notifyDataSetChanged();
						return;
					}
				}
	        }
	        
	    }
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
				String address =  msg.getData().getString("address");
				String deviceName =  msg.getData().getString("deviceName");
				
				String username = "User " + address;
				String profilePictureURI = PICTUREPATH+address;
				
				UserImpl userImpl = new UserImpl(deviceName, profilePictureURI, address);
				
				for(int i = 0; i < peopleNearby.size(); i++)
				{
					User user = peopleNearby.get(i);
					if(address.equals(user.getAddress()))
					{
						return;
					}
				}
				peopleNearby.add(userImpl);
				adapter.notifyDataSetChanged();
				
				// Start a HTTP request to get the username from server based on Bluetooth address
				new HTTPNameRequest().execute(address);
				
				break;
			case BTService.MSG_REGISTERED_CLIENT:
				System.err.println("startdiscovery");
				sendMessageToService("startDiscovery", "", BTService.MSG_START_DISCOVERY);
				break;
				
			case BTService.CONNECTION_FAILED:
				Toast.makeText(getApplicationContext(), "Could not connect at the moment. Try again.", Toast.LENGTH_SHORT).show();
				break;
				
			case BTService.START_CHAT_AVTIVITY:
				Intent intent = new Intent(getApplicationContext() , ChatActivity.class);
				Bundle b = new Bundle();
				b.putString("username", USERNAME);
				b.putString("receiver", ADDRESS);// TODO needs to come from the network
				b.putString("address", ADDRESS); 
				intent.putExtras(b);
				startActivity(new Intent(getApplicationContext(), ChatActivity.class));
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
			imageLoader.displayImage(user.getProfilePicURI(), profilePicture, options);

			return row;
		}
	}
	
}
