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

public class MainView extends Activity {
	private BroadcastReceiver mReceiver;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		
		Button siButton = (Button) findViewById(R.id.sendIntent);
		siButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

					getApplicationContext().sendBroadcast(new BTIntent());
			}
		});
		
		Button dbBbutton = (Button) findViewById(R.id.goToDBView);
		dbBbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			     startActivity(new Intent(getApplicationContext(),  DBView.class));
				
			}
		});
		

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

	
}