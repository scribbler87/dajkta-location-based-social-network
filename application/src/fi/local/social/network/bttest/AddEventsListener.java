package fi.local.social.network.bttest;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import fi.local.social.network.R;

final class AddEventsListener implements OnClickListener {
	/**
	 * 
	 */
	private final BTActivity btActivity;

	/**
	 * @param btActivity
	 */
	AddEventsListener(BTActivity btActivity) {
		this.btActivity = btActivity;
	}

	@Override
	public void onClick(View v) {
		TextView view = (TextView) this.btActivity.findViewById(R.id.eventNameField);
		String message = view.getText().toString();
		this.btActivity.events.put("" + this.btActivity.idSeq, message);
		this.btActivity.mConversationArrayAdapter.add("Added event " + this.btActivity.idSeq
				+ BTActivity.KEY_VALUE_SEPARATOR + message);
		this.btActivity.idSeq = this.btActivity.random.nextInt();

		System.out.println("Add events");
	}
}