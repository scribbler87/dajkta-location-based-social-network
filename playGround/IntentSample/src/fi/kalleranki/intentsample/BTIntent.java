package fi.kalleranki.intentsample;

import android.content.Intent;

public class BTIntent extends Intent {
	public static final String BT_INTENT = "BTintent";

	public BTIntent() {
		setAction(BT_INTENT);
		setData(null);
	}
}
