package fi.local.social.network.activities;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;			

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import fi.local.social.network.R;

public class NewEvent extends Activity {
	
	private static final int SELECT_PICTURE = 1;
	private EditText title=null;
	private EditText content=null;
	private ImageView image=null;
	private Button chooseImageBtn=null;
	private Button sendBtn=null;
	private Bitmap bitmap=null;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newevent);
		title=(EditText)findViewById(R.id.newEventTitle);
		content=(EditText)findViewById(R.id.newEventContent);
		image=(ImageView)findViewById(R.id.imgView);
		chooseImageBtn=(Button)findViewById(R.id.chooseImageBtn);
		sendBtn=(Button)findViewById(R.id.sendBtn);
		Bitmap bitmapTemp=(Bitmap)getLastNonConfigurationInstance();
		if(bitmapTemp!=null){
			image.setImageBitmap(bitmapTemp);
			bitmap=bitmapTemp;
		}
		
		//When a user clicks on the 'choose a image' button, the user is directed into the media folder to choose a image
		chooseImageBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//Launch an image choosing activity
				Intent imageChoosingIntent = new Intent(Intent.ACTION_PICK,
			               android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(imageChoosingIntent, SELECT_PICTURE);
			}
		});
		
		//When a user clicks on the 'Send Event' button, the newly created event would be sent to all nearby devices
		sendBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(),
						"The message is supposed to be sent to nearby devices.",
						Toast.LENGTH_SHORT).show();
			}
		});
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
	            bitmap=yourSelectedImage;//Store the bitmap of image in case of screen rotation
	        }
	    }
	}
	
	//Save the bitmap representation of the image when screen rotates and restore it upon completion of rotation.
    @Override
    public Object onRetainNonConfigurationInstance(){
    	return bitmap;
    }
}
