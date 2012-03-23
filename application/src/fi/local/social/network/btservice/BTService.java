package fi.local.social.network.btservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class BTService extends Service {

	private final IBinder mBinder = new BTServiceBinder();

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	public class BTServiceBinder extends Binder {

		BTService getService() {
			return BTService.this;
		}
	}
}
