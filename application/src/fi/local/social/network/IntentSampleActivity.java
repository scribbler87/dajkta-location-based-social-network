package fi.local.social.network;

import fi.local.social.network.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class IntentSampleActivity extends Activity implements OnClickListener {
	private BroadcastReceiver mReceiver;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(this);

		this.mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				Log.i("IntentSampleActivity", "Received intent");

				
			}
		};

		IntentFilter filter = new IntentFilter(BTIntent.BT_INTENT);
		registerReceiver(mReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	@Override
	public void onClick(View button) {
		getApplicationContext().sendBroadcast(new BTIntent());
	}
}