package fi.local.social.network.activities;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import fi.local.social.network.R;
import fi.local.social.network.btservice.BTService;
import fi.local.social.network.db.ChatMessage;
import fi.local.social.network.db.ChatMessagesDataSource;
import fi.local.social.network.db.User;
import fi.local.social.network.db.UserDataSource;
import fi.local.social.network.db.UserImpl;

public class SettingActivity extends Activity {
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

		// Save the new comment to the database
		// TODO add own device address
		User newUser = new UserImpl(newUsername, "not yet here",
				"dummy adrress");
		newUser.setIsPhoneUser(true);

		List<User> allEntries = userDataSource.getAllEntries();
		User oldUser = null;
		for (User actUser : allEntries) {
			if (actUser.isPhoneUser()) {
				oldUser = actUser;
				userDataSource.deleteUser(actUser);
			}
		}

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
	
}
