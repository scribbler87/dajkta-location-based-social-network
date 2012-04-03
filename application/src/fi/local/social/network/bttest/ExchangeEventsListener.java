package fi.local.social.network.bttest;

import android.view.View;
import android.view.View.OnClickListener;

final class ExchangeEventsListener implements OnClickListener {
	/**
	 * 
	 */
	private final BTActivity btActivity;

	/**
	 * @param btActivity
	 */
	ExchangeEventsListener(BTActivity btActivity) {
		this.btActivity = btActivity;
	}

	@Override
	public void onClick(View v) {
		this.btActivity.startExchange();
	}
}