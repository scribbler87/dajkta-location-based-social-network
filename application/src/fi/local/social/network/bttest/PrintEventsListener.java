package fi.local.social.network.bttest;

import android.view.View;
import android.view.View.OnClickListener;

final class PrintEventsListener implements OnClickListener {
	/**
	 * 
	 */
	private final BTActivity btActivity;

	/**
	 * @param btActivity
	 */
	PrintEventsListener(BTActivity btActivity) {
		this.btActivity = btActivity;
	}

	@Override
	public void onClick(View v) {
		StringBuilder allEvents = new StringBuilder();
		allEvents.append("Events we have:");
		for (String s : this.btActivity.events.keySet()) {
			allEvents.append("" + s + BTActivity.KEY_VALUE_SEPARATOR + this.btActivity.events.get(s)
					+ BTActivity.PHASE_CONTENT_SEPARATOR);
		}
		this.btActivity.mConversationArrayAdapter.add(allEvents.toString());
	}
}