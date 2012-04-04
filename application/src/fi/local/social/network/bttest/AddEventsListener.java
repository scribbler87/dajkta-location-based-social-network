package fi.local.social.network.bttest;

import java.util.Random;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import fi.local.social.network.R;

final class AddEventsListener implements OnClickListener {
	/**
	 * 
	 */
	private final BTActivity btActivity;
	private Random random = new Random();

	/**
	 * @param btActivity
	 */
	AddEventsListener(BTActivity btActivity) {
		this.btActivity = btActivity;
	}

	@Override
	public void onClick(View v) {
		TextView view = (TextView) this.btActivity
				.findViewById(R.id.eventNameField);
		BTContent content = new BTContentImpl(view.getText().toString());
		BTMessageImpl message = new BTMessageImpl(content);
		this.btActivity.getEvents().add(message);
		this.btActivity.mConversationArrayAdapter.add("Added event "
				+ message.getMessage());

		System.out.println("Add events");
	}
}