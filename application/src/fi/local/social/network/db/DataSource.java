package fi.local.social.network.db;

import java.util.List;



public interface DataSource {

	public void open();
	public void close();
	public Object createEntry(String entry);
	public List<?> getAllEntries();
	
	
}
