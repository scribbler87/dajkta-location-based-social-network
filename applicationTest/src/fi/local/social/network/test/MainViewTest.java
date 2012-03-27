package fi.local.social.network.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;
import fi.local.social.network.views.*;

public class MainViewTest extends ActivityInstrumentationTestCase2<MainView> 
{

	public MainViewTest()
	{
		super("fi.local.social.network", MainView.class);
		//super("fi.local.social.network.IntentSampleActivity", IntentSampleActivity.class);
	}
	
	private MainView mActivity;
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