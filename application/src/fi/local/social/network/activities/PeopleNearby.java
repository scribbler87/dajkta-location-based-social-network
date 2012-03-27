package fi.local.social.network.activities;
import fi.local.social.network.R;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PeopleNearby extends ListActivity {
	static final String[] PEOPLE_NEAR=new String[]{
		   "Alex Yang","Tom Cruise","Tom Hanks","Jason Stathon","Joe Hu",
		   "Alex Yang","Tom Cruise","Tom Hanks","Jason Stathon","Joe Hu",
		   "Alex Yang","Tom Cruise","Tom Hanks","Jason Stathon","Joe Hu",
		   "Alex Yang","Tom Cruise","Tom Hanks","Jason Stathon","Joe Hu"};
			@Override
			public void onCreate(Bundle savedInstanceState){
				super.onCreate(savedInstanceState);
				setListAdapter((ListAdapter) new ArrayAdapter<String>(this, R.layout.peoplenearby,PEOPLE_NEAR));
				ListView listView = getListView();
				listView.setTextFilterEnabled(true);
				
				listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					// When clicked, show a toast with the TextView text
					Toast.makeText(getApplicationContext(),
					((TextView) view).getText(), Toast.LENGTH_SHORT).show();
					}
					});
			}//end onCreate
			
			@Override
			public boolean onCreateOptionsMenu(Menu menu){
				MenuInflater inflater=getMenuInflater();
				inflater.inflate(R.layout.menu_people_nearby, menu);
				return true;
			}
}
