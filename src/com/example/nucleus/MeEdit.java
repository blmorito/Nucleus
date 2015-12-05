package com.example.nucleus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.nucleus.DashboardMe.getProfileData;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MeEdit extends Activity {
	private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
	Integer user_id;
	
	Bitmap bm;
	Boolean toUpload;
	Boolean toUpdate;
	String pathx;
	
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	SharedPreferences pref;
	
	ImageView img_prof;
	EditText edt_fname, edt_address;
	Button btn_save, btn_cancel, btn_upload;
	TextView txt_changeCred;
	
	String temp_full_name, temp_address;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.me_profile_edit);
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		
		pref = getSharedPreferences("MyPref", MODE_PRIVATE);
		user_id = pref.getInt("user_id", 0);
		
		toUpload = false;
		toUpdate = false;
		
		//getting passed intent values
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		     temp_full_name = extras.getString("full_name");
		     temp_address = extras.getString("address");
		}
		
		img_prof = (ImageView) findViewById(R.id.img_editProf);
		edt_fname = (EditText) findViewById(R.id.edt_editProfFull);
		edt_address = (EditText) findViewById(R.id.edt_editProfAddr);
		btn_save = (Button) findViewById(R.id.btn_editProfSave);
		btn_cancel = (Button) findViewById(R.id.btn_editProfCancel);
		btn_upload = (Button) findViewById(R.id.btn_editProfUpload);
		txt_changeCred = (TextView) findViewById(R.id.txt_editProfChangePW);
		
		edt_fname.setText(temp_full_name);
		edt_address.setText(temp_address);
		
		new NetCheck().execute();
		//edt listeners
		
		txt_changeCred.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MeEdit.this, MeEditCred.class));
			}
		});
		
		edt_fname.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edt_fname.setError(null);
			}
		});
		edt_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edt_address.setError(null);
			}
		});
		btn_save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(edt_fname.getText().toString().equalsIgnoreCase("") && edt_address.getText().toString().equalsIgnoreCase("")){
					edt_fname.setError("This field cannot be blank");
					edt_address.setError("This field cannot be blank");
				}else if (edt_fname.getText().toString().equalsIgnoreCase("")) {
					edt_fname.setError("This field cannot be blank");
				}else if(edt_address.getText().toString().equalsIgnoreCase("")) {
					edt_address.setError("This field cannot be blank");
				}else{
					toUpdate = true;
					new NetCheck().execute();
				}
			}
		});
		
		btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				finish();
			}
		});
		
		btn_upload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent galleryIntent = new Intent(Intent.ACTION_PICK,
		                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		        // Start the Intent
		        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
			}
		});
		
		
		
		
		
		
	}
	public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
	    int targetWidth = 250;
	    int targetHeight = 250;
	    Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, 
	                        targetHeight,Bitmap.Config.ARGB_8888);

	    Canvas canvas = new Canvas(targetBitmap);
	    Path path = new Path();
	    path.addCircle(((float) targetWidth - 1) / 2,
	        ((float) targetHeight - 1) / 2,
	        (Math.min(((float) targetWidth), 
	        ((float) targetHeight)) / 2),
	        Path.Direction.CCW);

	    canvas.clipPath(path);
	    Bitmap sourceBitmap = scaleBitmapImage;
	    canvas.drawBitmap(sourceBitmap, 
	        new Rect(0, 0, sourceBitmap.getWidth(),
	        sourceBitmap.getHeight()), 
	        new Rect(0, 0, targetWidth, targetHeight), null);
	    return targetBitmap;
	}
	
	 @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data
 
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
 
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();
 
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                
                Bitmap temp = BitmapFactory.decodeFile(imgDecodableString);
                bm = temp;
                temp = getRoundedShape(temp);
                // Set the Image in ImageView after decoding the String
//                img_prof.setImageBitmap(BitmapFactory
//                        .decodeFile(imgDecodableString));
                img_prof.setImageBitmap(temp);
                String filez[] = imgDecodableString.split("/");
                pathx = filez[filez.length - 1];
                
                if (imgDecodableString!=null && !imgDecodableString.isEmpty()){
                	toUpload = true;
                    new NetCheck().execute();
                }else{
                	Toast.makeText(getBaseContext(), "You didnt select an image from the gallery.", Toast.LENGTH_SHORT).show();
                }
 
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
 
    }
	public class NetCheck extends AsyncTask<Void, Void, Boolean>{
		private ProgressDialog pDialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            
            
            if (netInfo != null && netInfo.isConnected()) {
            	
                try {
                	
                    URL url = new URL("http://"+ip+"/nucleus/api/check_net.php");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    Log.d("wrongurl",e1.toString());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.d("wrongurl",e.toString());
                }
            }
            
            return false;
		}
		
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result == true){
				setProgressBarIndeterminateVisibility(false);
                
                
               
                if(toUpload==true){
                	toUpload = false;
                	new uploadAvatar().execute();
                }else if (toUpdate==true) {
					toUpdate = false;
					new updateProfile().execute();
				}else{
                	new getAvatar().execute();
                }
                
                
                //new loginAccount().execute();
                //Toast.makeText(getBaseContext(), "Connection Active", Toast.LENGTH_SHORT).show();
                
            }
            else{
            	setProgressBarIndeterminateVisibility(false);
            	
            	Toast.makeText(getBaseContext(), "Unable to connect to network. Please try again later", Toast.LENGTH_SHORT).show();
            	
                
                
            }
		}
		
	}
	public class getAvatar extends AsyncTask<Void, Void, JSONObject>{
		
		Integer u_id;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			u_id = user_id;
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			userFunctions uf = new userFunctions();
			JSONObject json = uf.getAvatarForEdit(u_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					
					
					String bm = json.getString("avatar");
					byte[] blobimg = Base64.decode(bm.getBytes(), 1);
			    	Bitmap bitmap = BitmapFactory.decodeByteArray(blobimg, 0, blobimg.length);
			    	
			    	img_prof.setImageBitmap(getRoundedShape(bitmap));
					
			    	
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					if (json.getInt("errorCode")==1){
						
						Toast.makeText(MeEdit.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}else{
						
						Toast.makeText(MeEdit.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + json);
			}
		}
		
	}
	public class uploadAvatar extends AsyncTask<Void, Void, JSONObject>{
		
		//Integer u_id;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			BitmapFactory.Options options = null;
            options = new BitmapFactory.Options();
            options.inSampleSize = 3;
            Bitmap bitmap;
            bitmap = BitmapFactory.decodeFile(imgDecodableString,
                    options);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Must compress the Image to reduce image size to make upload easy
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream); 
            byte[] byte_arr = stream.toByteArray();
            // Encode Image to String
            pathx = Base64.encodeToString(byte_arr, 0);
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.e("64", pathx.length()+"");
			userFunctions uf = new userFunctions();
			JSONObject json = uf.uploadAvatar(pathx, user_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					
					
					Toast.makeText(getBaseContext(), "Avatar successfully uploaded", Toast.LENGTH_SHORT).show();
					
			    	
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					if (json.getInt("errorCode")==1){
						
						Toast.makeText(MeEdit.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}else{
						
						Toast.makeText(MeEdit.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + json);
			}
		}
		
	}
	public class updateProfile extends AsyncTask<Void, Void, JSONObject>{
		
		//Integer u_id;
		String name;
		String address;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			name = edt_fname.getText().toString();
			address = edt_address.getText().toString();
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			//Log.e("64", pathx.length()+"");
			userFunctions uf = new userFunctions();
			JSONObject json = uf.updateProfile(name, address, user_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					
					
					Toast.makeText(getBaseContext(), "You have successfully updated your profile.", Toast.LENGTH_SHORT).show();
					finish();
			    	
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					if (json.getInt("errorCode")==1){
						
						Toast.makeText(MeEdit.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}else{
						
						Toast.makeText(MeEdit.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + json);
			}
		}
		
	}
}
