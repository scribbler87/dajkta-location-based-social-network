package fi.local.social.network.activities;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import fi.local.social.network.R;

public class SettingActivity extends Activity {
	private static final int SELECT_PICTURE = 1;
	private ImageView image;
	private Button selectPic;
	private Button saveBtn;
	private TextView nickname;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		
		//selectPic=(Button)findViewById(R.id.chooseProfilePicBtn);
		image=(ImageView)findViewById(R.id.imageView1);
		saveBtn=(Button)findViewById(R.id.saveBtn);
		nickname=(TextView)findViewById(R.id.etNickname);
		/*
		selectPic.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//Launch an image choosing activity
				
			}
		});
		*/
		
		
		
		//When a user clicks on the 'Save' button, changed settings would be saved.
		saveBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(),
						"Your nickname is "+nickname.getText().toString(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}
	//When a user clicks on the 'choose a image' button, the user is directed into the media folder to choose a image
	public void chooseProfilePic(View v)
	{
		Intent imageChoosingIntent = new Intent(Intent.ACTION_PICK,
	               android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(imageChoosingIntent, SELECT_PICTURE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
	    super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

	    switch(requestCode) { 
	    case SELECT_PICTURE:
	        if(resultCode == RESULT_OK){  
	        	//Uri of the selected image by user
	            Uri selectedImage = imageReturnedIntent.getData();
	            InputStream imageStream=null;
				try {
					imageStream = getContentResolver().openInputStream(selectedImage);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//Get bitmap format of the selected image
	            Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
	            //Show the image in the ImageView
	            image.setImageBitmap(yourSelectedImage);
	        }
	    }
	}
	
	
}
