package fi.local.social.network.activities;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.example.android.actionbarcompat.ActionBarActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import fi.local.social.network.R;
import fi.local.social.network.activities.PeopleActivity.HTTPNameRequest;
import fi.local.social.network.btservice.BTService;
import fi.local.social.network.db.ChatMessage;
import fi.local.social.network.db.ChatMessagesDataSource;
import fi.local.social.network.db.User;
import fi.local.social.network.db.UserDataSource;
import fi.local.social.network.db.UserImpl;

public class SettingActivity extends ActionBarActivity {
	
	private static String AddUserURL= "http://www.vugi.iki.fi/msp-api/addUser.php";
	
	private static final int SELECT_PICTURE = 1;
	private ImageView image;
	// private Button selectPic;
	private Button saveBtn;
	private EditText nickname;
	private UserDataSource userDataSource;
	private ChatMessagesDataSource chatMessDataSource;
	private Button stopServiceButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		// selectPic=(Button)findViewById(R.id.chooseProfilePicBtn);
		image = (ImageView) findViewById(R.id.imageView1);
		saveBtn = (Button) findViewById(R.id.saveBtn);
		nickname = (EditText) findViewById(R.id.etNickname);

		userDataSource = new UserDataSource(this);


		// selectPic=(Button)findViewById(R.id.chooseProfilePicBtn);
		image = (ImageView) findViewById(R.id.imageView1);
		saveBtn = (Button) findViewById(R.id.saveBtn);
		nickname = (EditText) findViewById(R.id.etNickname);

		// When a user clicks on the 'Save' button, changed settings would be
		// saved.
		saveBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveNickname();
			}
		});

		nickname.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					saveNickname();
					return true;
				}

				return false;
			}
		});
		
		
		stopServiceButton = (Button) findViewById(R.id.stopService);
		
		if(BTService.isRunning())
			stopServiceButton.setText("Stop-Service");
		else
			stopServiceButton.setText("Start-Service");
			
		
		stopServiceButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if("Stop-Service".equals(stopServiceButton.getText()))
				{
					stopService(new Intent(SettingActivity.this, BTService.class));
					stopServiceButton.setText("Start-Service");
				}
				else
				{
					startService(new Intent(SettingActivity.this, BTService.class));
					stopServiceButton.setText("Stop-Service");
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		userDataSource.open();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		userDataSource.close();
	}
	
	// When a user clicks on the 'choose a image' button, the user is directed
	// into the media folder to choose a image
	public void chooseProfilePic(View v) {
		Intent imageChoosingIntent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(imageChoosingIntent, SELECT_PICTURE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {
		case SELECT_PICTURE:
			if (resultCode == RESULT_OK) {
				// Uri of the selected image by user
				Uri selectedImage = imageReturnedIntent.getData();
				InputStream imageStream = null;
				try {
					imageStream = getContentResolver().openInputStream(
							selectedImage);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Get bitmap format of the selected image
				Bitmap yourSelectedImage = BitmapFactory
						.decodeStream(imageStream);
				// Show the image in the ImageView
				image.setImageBitmap(yourSelectedImage);
			}
		}
	}

	private void saveNickname() {
		String newUsername = nickname.getText().toString();
		Toast.makeText(getApplicationContext(),
				"Your nickname is " + newUsername, Toast.LENGTH_SHORT).show();

		// Get the own device address
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		String btAddress = btAdapter.getAddress();
		Toast.makeText(getApplicationContext(),
				"Your BT address is " + btAddress, Toast.LENGTH_SHORT).show();
		
		// Save user information to server
		new HTTPSaveSettings().execute(btAddress,newUsername);
		
		// Save the new user to the database
		User newUser = new UserImpl(newUsername, "", btAddress);
		newUser.setIsPhoneUser(true);

		// Remove old user
		List<User> allEntries = userDataSource.getAllEntries();
		User oldUser = null;
		for (User actUser : allEntries) {
			if (actUser.isPhoneUser()) {
				oldUser = actUser;
				userDataSource.deleteUser(actUser);
			}
		}

		// Update chat messages
		chatMessDataSource = new ChatMessagesDataSource(this);
		chatMessDataSource.open();
		List<ChatMessage> chatMessages = chatMessDataSource.getAllEntries();
		for (ChatMessage chatMessage : chatMessages) {
			chatMessDataSource.deleteChatMessage(chatMessage);

			if (chatMessage.getReceiverName().equals(oldUser.getUserName())) {
				chatMessage.setReceiverName(newUsername);
			} else if (chatMessage.getSenderName()
					.equals(oldUser.getUserName())) {
				chatMessage.setSenderName(newUsername);
			}

			chatMessDataSource.createEntry(chatMessage.getDBString());

		}
		chatMessDataSource.close();

		userDataSource.createEntry(newUser.getDBString());

		startActivity(new Intent(getApplicationContext(), PeopleActivity.class));
	}
	
class HTTPSaveSettings extends AsyncTask<String, String, String>{
		
		
		private String address;
		private String name;
		private String TAG = "HTTPSaveSettings";

	    @Override
	    protected String doInBackground(String... params) {
	    	
	    	this.address = params[0];
	    	this.name = params[1];
	    	Log.d(TAG,"Starting request for address: " + address);
	    	
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpPost httppost = new HttpPost(SettingActivity.AddUserURL);
	        
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("address", address));
	        nameValuePairs.add(new BasicNameValuePair("name", name));
	        try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        
	        HttpResponse response;
	        String responseString = null;
	        try {
	            response = httpclient.execute(httppost);
	            StatusLine statusLine = response.getStatusLine();
	            
	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                responseString = out.toString();
	            } else{
	                response.getEntity().getContent().close();
	                throw new IOException(statusLine.getReasonPhrase());
	            }
	        } catch (IOException e) {
	        	Log.e(TAG,"Got IOException");
	            //TODO Handle problems..
	        }
	        return responseString;
	    }

	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        if (result != null){
	        	String msg = "Name saved to server: " + name;
	        	Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	        	Log.d(TAG,msg);
	        	Log.d(TAG,result);
	        }
	        
	    }
	}
	
}
