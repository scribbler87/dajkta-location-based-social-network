package fi.local.social.network.btservice;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import fi.local.social.network.btservice.BTService.BTServiceBinder;

/**
 * Adds protected field mService for communicating with the bluetooth service.
 * 
 * @author kranki
 * 
 */
public class BTServiceConnectedActivity extends Activity {

	protected BTServiceInterface mService;

	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBound = false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			BTServiceBinder binder = (BTServiceBinder) service;
			mService = (BTServiceInterface) binder.getService();
			mBound = true;
		}
	};
	private boolean mBound = false;

	public BTServiceConnectedActivity() {
		super();
	}

	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = new Intent(this, BTService.class);
		bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mBound) {
			unbindService(mServiceConnection);
			mBound = false;
		}
	}
}