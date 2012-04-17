package fi.local.social.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import fi.local.social.network.btservice.BTActivity;
import fi.local.social.network.btservice.BTServiceConnectedActivity;

public class MainActivity extends BTServiceConnectedActivity {
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("IntentSampleActivity", "Received intent");
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		bindSendIntentButton(); // button for sending BTI intents
		bindDbButton(); // Button for testing db.
		bindBTButton(); // Testing bt

		IntentFilter filter = new IntentFilter(BTIntent.BT_INTENT);
		registerReceiver(mReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	// helper methods

	private void bindBTButton() {
		Button BTButton = (Button) findViewById(R.id.btButton);
		BTButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						BTActivity.class));
			}
		});

	}

	private void bindSendIntentButton() {
		Button sendIntentButton = (Button) findViewById(R.id.sendIntent);
		sendIntentButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getApplicationContext().sendBroadcast(new BTIntent());
			}
		});
	}

	private void bindDbButton() {
		Button dbBbutton = (Button) findViewById(R.id.goToDBView);
		dbBbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), DBView.class));
			}
		});
	}

}