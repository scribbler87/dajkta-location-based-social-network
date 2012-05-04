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
import fi.local.social.network.R;
import fi.local.social.network.btservice.BTService;
import fi.local.social.network.btservice.BTService;
import fi.local.social.network.db.EventImpl;
import fi.local.social.network.db.User;
import fi.local.social.network.db.UserDataSource;
import fi.local.social.network.db.UserImpl;
import com.example.android.actionbarcompat.*;

public class PeopleActivity extends ActionBarActivity {
	
	List<User> peopleNearby;
	private UserDataSource userDatasource;
	public static String USERNAME = "";
	final Messenger mMessenger = new Messenger(new IncomingHandler()); // Target we publish for clients to send messages to IncomingHandler.
	private ArrayAdapter<User> adapter;


	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.people);
		
		// TODO: get real people and their names
		// add some mockup values
		peopleNearby = new ArrayList<User>();
//		peopleNearby.add(new UserImpl("Tom Cruise", "add uri for pic"));
//		peopleNearby.add(new UserImpl("Tom Hanks", "add uri for pic"));
//		peopleNearby.add(new UserImpl("Jason Stathon","add uri for pic"));
//		peopleNearby.add(new UserImpl("Joe Hu", "add uri for pic"));

		adapter = new ArrayAdapter<User>(this, R.layout.people_item, R.id.label, peopleNearby);
		
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
		ComponentName startService = startService(new Intent(PeopleActivity.this, BTService.class));
		if(startService != null)
		{
			doBindService();
			sendMessageToService("startdiscovery", "", BTService.MSG_START_DISCOVERY);
		}
		else
			System.err.println("NULL");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			System.err.println("stop service");
			stopService(new Intent(PeopleActivity.this, BTService.class));
			doUnbindService();
		} catch (Throwable t) {
			Log.e("MainActivity", "Failed to unbind from the service", t);
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

	// staff for connection to service***************************
	Messenger mService = null;
	boolean mIsBound;

	private String TAG = "Service Connection";

	private ServiceConnection mConnection = new ServiceConnection() {
		

		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = new Messenger(service);
			Log.i(TAG ,"Attached.");
				sendMessageToService("msg_register", "", BTService.MSG_REGISTER_CLIENT);
				// In this case the service has crashed before we could even do anything with it
		}
		
		
		// *********** can be used to send a message to another device
		public void sendMsg(Message m)
		{
			try {
				mService.send(m);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been unexpectedly disconnected - process crashed.
			mService = null;
			Log.i(TAG , "Disconnected.");
		}
	};

	void doBindService() {
		bindService(new Intent(this, BTService.class), mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
		Log.i(TAG ,"Binding.");
	}
	void doUnbindService() {
		if (mIsBound) {
			// If we have received the service, and hence registered with it, then now is the time to unregister.
			if (mService != null) {
				try {
					Message msg = Message.obtain(null, BTService.MSG_UNREGISTER_CLIENT);
					mService.send(msg);
				} catch (RemoteException e) {
					// There is nothing special we need to do if the service has crashed.
				}
			}
			// Detach our existing connection.
			unbindService(mConnection);
			mIsBound = false;
			Log.i(TAG ,"Unbinding.");
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
            	String address = msg.getData().getString("address");
            	UserImpl userImpl = new UserImpl(address, "add to uri", address);
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
                
            default:
                super.handleMessage(msg);
            }
        }
    }
    
    private void sendMessageToService(String key, String data, int MSG) {
        if (mIsBound) {
            if (mService != null) {
                try {
        			
        			Bundle b = new Bundle();
        			b.putString(key, data);
        			Message msg = Message.obtain(null, MSG);
        			msg.setData(b);
                	
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                }
            }
        }
    }
   
}
