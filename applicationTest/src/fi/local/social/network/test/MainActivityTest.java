package fi.local.social.network.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;
import fi.local.social.network.activities.*;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> 
{

	public MainActivityTest()
	{
		super("fi.local.social.network", MainActivity.class);
		//super("fi.local.social.network.IntentSampleActivity", IntentSampleActivity.class);
	}
	
	private MainActivity mActivity;
	private TextView mView;
	private String ressourceString;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mActivity = this.getActivity();
		mView = (TextView) mActivity.findViewById(fi.local.social.network.R.id.sendIntent );
		//ressourceString = mActivity.getString(fi.local.social.network.R.string.hello );
		ressourceString = "Send intent";
	}
	
	public void testPrecondition() 
	{
		assertNotNull(mView);
	}
	
	public void testText ()
	{
		assertEquals(ressourceString, (String)mView.getText());
	}
}