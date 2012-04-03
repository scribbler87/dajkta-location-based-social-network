package fi.local.social.network.bttest;

import java.util.Map;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;

final class PrintEventsListener implements OnClickListener {
	/**
	 * 
	 */
	private ArrayAdapter<String> mConversationArrayAdapter;
	private Map<String, String> events;

	/**
	 * @param btActivity
	 */
	PrintEventsListener(ArrayAdapter<String> conversationArrayAdapter,
			Map<String, String> events) {
		this.mConversationArrayAdapter = conversationArrayAdapter;
		this.events = events;
	}

	@Override
	public void onClick(View v) {
		StringBuilder allEvents = new StringBuilder();
		allEvents.append("Events we have:");
		for (String s : events.keySet()) {
			allEvents.append("" + s + BTActivity.KEY_VALUE_SEPARATOR
					+ events.get(s)
					+ BTActivity.PHASE_CONTENT_SEPARATOR);
		}
		this.mConversationArrayAdapter.add(allEvents.toString());
	}
}