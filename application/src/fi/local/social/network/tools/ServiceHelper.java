package fi.local.social.network.tools;

import com.example.android.actionbarcompat.ActionBarActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import fi.local.social.network.R;
import fi.local.social.network.activities.PeopleActivity;
import fi.local.social.network.btservice.BTService;

public abstract class ServiceHelper extends ActionBarActivity{

	// staff for connection to service***************************
	protected Messenger mService = null;
	protected boolean mIsBound = false;
	protected Messenger mMessenger;
	protected String TAG = "Service Connection";

	protected ServiceConnection mConnection = new ServiceConnection() {


		public void onServiceConnected(ComponentName className, IBinder service) {
			System.err.println("onServiceConnected");
			if(service != null)
				mService = new Messenger(service);
			try {
				Log.i(TAG , service.getInterfaceDescriptor());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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



	protected void sendMessageToService(String key, String data, int MSG) {
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
			else
				System.err.println("mService was null");
		}
	}

	protected void doBindService(Activity act) {
		
		
		ComponentName startService = startService(new Intent(act, BTService.class));
		

		
		if(BTService.isRunning())
		{
			System.err.println("bt service is running");
		}

		mIsBound = getApplicationContext().bindService(new Intent(this, BTService.class), mConnection, Context.BIND_AUTO_CREATE);
		
		
		Log.i(TAG ,"Binding.");
	}
	
	protected void doUnbindService() {
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
	
	protected void doUnRegister() {
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
			mIsBound = false;
			Log.i(TAG ,"Unbinding.");
		}
	}


}
