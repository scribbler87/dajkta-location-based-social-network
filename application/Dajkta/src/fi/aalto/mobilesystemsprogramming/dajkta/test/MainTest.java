package fi.aalto.mobilesystemsprogramming.dajkta.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;
import fi.aalto.mobilesystemsprogramming.dajkta.DajktaActivity;
import android.widget.TextView;
public class MainTest extends ActivityInstrumentationTestCase2<DajktaActivity> 
{

	public MainTest()
	{
		super("fi.aalto.mobilesystemprogramming.dajkta.DajktaActivity", DajktaActivity.class);
		
	}
	
	private DajktaActivity mActivity;
	private TextView mView;
	private String ressourceString;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mActivity = this.getActivity();
		mView = (TextView) mActivity.findViewById(fi.aalto.mobilesystemsprogramming.dajkta.R.id.textView01);
		ressourceString = mActivity.getString(fi.aalto.mobilesystemsprogramming.dajkta.R.string.hello);
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
