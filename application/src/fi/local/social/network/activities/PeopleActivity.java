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
import fi.local.social.network.btservice.BluetoothChatService;
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
				
				startActivityForResult( (new Intent(getApplicationContext(), DeviceListActivity.class)),
						BluetoothChatService.REQUEST_CONNECT_DEVICE );
				Message message = Message.obtain(null, BluetoothChatService.MSG_SEND_EVENT);
//				Bundle b = new Bundle();
//				b.putString("str1", buffer.toString());
				//		message.setData(b);
				try {
					mService.send(message);
				} catch (RemoteException e) {
					e.printStackTrace();
				}

				// TODO
//				Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
//				intent.putExtra("username", this.USERNAME);
//				Object o = this.getListAdapter().getItem(position);
//				String receiverName = o.toString();
//				intent.putExtra("receiver", receiverName.toString());
//				startActivity(intent);
				
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
		
		ComponentName startService = startService(new Intent(PeopleActivity.this, BluetoothChatService.class));
		if(startService != null)
		{
			
			doBindService();
		}
		else
			System.err.println("NULL");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			stopService(new Intent(PeopleActivity.this, BluetoothChatService.class));
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
		case R.id.device_list:
			startActivity(new Intent(getApplicationContext(), DeviceListActivity.class));
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
			try {
				Message msg = Message.obtain(null, BluetoothChatService.MSG_REGISTER_CLIENT);
				mService.send(msg);
				Message msg2 = Message.obtain(null, BluetoothChatService.MSG_SEND_EVENT);
				mService.send(msg2);
				
			} catch (RemoteException e) {
				// In this case the service has crashed before we could even do anything with it
			}
		}
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
		bindService(new Intent(this, BluetoothChatService.class), mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
		Log.i(TAG ,"Binding.");
	}
	void doUnbindService() {
		if (mIsBound) {
			// If we have received the service, and hence registered with it, then now is the time to unregister.
			if (mService != null) {
				try {
					Message msg = Message.obtain(null, BluetoothChatService.MSG_UNREGISTER_CLIENT);
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
//            case BluetoothChatService.MSG_SET_INT_VALUE:
//                //textIntValue.setText("Int Message: " + msg.arg1);
//                break;
            case BluetoothChatService.MSG_REC_EVENT:
                String str1 = msg.getData().getString("str1");
//                textStrValue.setText("Str Message: " + str1);
                Toast.makeText(getApplicationContext(), str1, Toast.LENGTH_SHORT).show();
                break;
            default:
                super.handleMessage(msg);
            }
        }
    }
    
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case BluetoothChatService.REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				// Get the device MAC address
				String address = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
//				// Get the BLuetoothDevice object
//				BluetoothDevice device = mBluetoothAdapter
//						.getRemoteDevice(address);
//				// Attempt to connect to the device
//				
//				mChatService.connect(device);
				Message msg2 = Message.obtain(null, BluetoothChatService.MSG_DEVICE_ADDRESS);
				Bundle b = new Bundle();
				b.putString("address", address);
				try {
					mService.send(msg2);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			break;

		}
	}
}
