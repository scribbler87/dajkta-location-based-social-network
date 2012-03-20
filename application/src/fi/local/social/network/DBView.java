package fi.local.social.network;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DBView extends Activity implements OnClickListener{
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dbview);
		
		Button goBackButton = (Button) findViewById(R.id.buttonDBgoBack);
		goBackButton.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		super.onBackPressed();
	}
}
