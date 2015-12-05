package com.example.nucleus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.nucleus.Dashboard.ActivitiesAdapter;
import com.example.nucleus.Dashboard.getWorkspaceName;
import com.example.nucleus.Dashboard.ActivitiesAdapter.myViewHolder;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardMe extends Activity {
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	SharedPreferences prefs;
	
	Integer user_id, workspace_id;
	Integer profile_id;
	Boolean ownProfile;
	String currentP;
	//views
	ImageView img_profilePic;
	TextView txt_pName, txt_pAddress, txt_pEmail;
	Button btn_pEdit;
	
	LinearLayout checkConnect, layoutProfile;
	TextView txt_lvLabel;
	
	//lisss
	LayoutInflater inflater;
	ListView lv;
	
	JSONArray activities;
	//Bitmap bitmap;
	
	//public List<Bitmap> ac_avatar = new ArrayList<Bitmap>();
	public List<String> ac_pname = new ArrayList<String>();
	public List<String> ac_fname = new ArrayList<String>();
	public List<String> ac_content = new ArrayList<String>();
	public List<String> ac_date = new ArrayList<String>();
	
	//ee
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.me_profile);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		
		prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
		
		user_id = prefs.getInt("user_id", 0);
		workspace_id = prefs.getInt("workspace_id", 0);
		
		Log.e("chk again", ""+user_id);
		String value="";
		String profile_name = "";
		//getting passed intent values
		lv = (ListView) findViewById(R.id.listView1);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			
			Log.e("muzz", extras.toString());
		     value = extras.getString("user_id");
		     profile_id = Integer.parseInt(value);
		     profile_name= extras.getString("profile_name");
		     
		     Log.e("chk 1",""+profile_id.getClass().getName()+" | "+user_id.getClass().getName());
		}else{
			profile_id = user_id;
		}
		
		txt_lvLabel = (TextView) findViewById(R.id.textView4);
		Log.e("logz", ""+profile_id);
		
		if(user_id.equals(profile_id)){
			Log.e("hhhhh", ""+user_id+" | "+profile_id);
			ownProfile=true;
			
			Log.e("muzzy","nisud sa true dri");
		}else{
			Log.e("hhhhh", ""+user_id+" | "+profile_id);
			ownProfile=false;
			setTitle(profile_name);
			Log.e("muzzy","nisud sa false dri");
		}
		
		Log.e("hmm",""+ownProfile);
		
		//view initializations
		checkConnect = (LinearLayout) findViewById(R.id.no_connection_layout_profile);
		layoutProfile = (LinearLayout) findViewById(R.id.profile);
		img_profilePic = (ImageView) findViewById(R.id.img_mePic);
		txt_pName = (TextView) findViewById(R.id.txt_meName);
		txt_pAddress = (TextView) findViewById(R.id.txt_meAddress);
		txt_pEmail = (TextView) findViewById(R.id.txt_meEmail);
		btn_pEdit = (Button) findViewById(R.id.btn_meEdit);
		
		if (ownProfile==false){
			btn_pEdit.setVisibility(View.GONE);
		}
		
		new NetCheck().execute();
		
		btn_pEdit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(DashboardMe.this, MeEdit.class);
				i.putExtra("full_name", txt_pName.getText().toString());
				i.putExtra("address", txt_pAddress.getText().toString());
				startActivity(i);
			}
		});;
		
		
		
		
		
		
		
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
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		new NetCheck().execute();
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
                
                
                runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						checkConnect.setVisibility(LinearLayout.GONE);
						
					}
				});
                
                new getProfileData().execute();
                
                //new loginAccount().execute();
                //Toast.makeText(getBaseContext(), "Connection Active", Toast.LENGTH_SHORT).show();
                
            }
            else{
            	setProgressBarIndeterminateVisibility(false);
            	
            	runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						checkConnect.setVisibility(LinearLayout.VISIBLE);
						layoutProfile.setVisibility(LinearLayout.GONE);
					}
				});
            	
                
                
            }
		}
		
	}
	
	public class getProfileData extends AsyncTask<Void, Void, JSONObject>{
		
		Integer u_id;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			u_id = profile_id;
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			userFunctions uf = new userFunctions();
			JSONObject json = uf.getProfileData(u_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					txt_pName.setText(json.getString("full_name"));
					txt_pAddress.setText(json.getString("address"));
					txt_pEmail.setText(json.getString("email"));
					
					String bm = json.getString("avatar");
					byte[] blobimg = Base64.decode(bm.getBytes(), 1);
			    	Bitmap bitmap = BitmapFactory.decodeByteArray(blobimg, 0, blobimg.length);
			    	
			    	img_profilePic.setImageBitmap(getRoundedShape(bitmap));
					
			    	layoutProfile.setVisibility(LinearLayout.VISIBLE);
			    	
			    	new getUserActivities().execute();
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					if (json.getInt("errorCode")==1){
						
						Toast.makeText(DashboardMe.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}else{
						
						Toast.makeText(DashboardMe.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + json);
			}
		}
		
	}
	
	public class getUserActivities extends AsyncTask<Void, Void, JSONObject>{
		
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			userFunctions uf = new userFunctions();
			JSONObject json = uf.getUserActivities(profile_id, user_id, workspace_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					activities = json.getJSONArray("activities");
					
					//ac_avatar.clear();
					ac_pname.clear();
					ac_fname.clear();
					ac_date.clear();
					ac_content.clear();
					
					
					for (int i = 0; i<activities.length(); i++){
						
						JSONObject z = activities.getJSONObject(i);
						String pname = z.getString("act_pname");
						String fname = z.getString("act_fname");
						String content = z.getString("act_content");
						String agodate = z.getString("act_date");
						//String avatar = z.getString("act_avatar");
						
						ac_pname.add(pname);
						ac_fname.add(fname);
						ac_content.add(content);
						ac_date.add(agodate);
						//byte[] blobimg = Base64.decode(avatar.getBytes(), 1);
				    	//bitmap = BitmapFactory.decodeByteArray(blobimg, 0, blobimg.length);
						
				    	//ac_avatar.add(bitmap);
						
					}
					
					
					currentP = ac_pname.get(0);
					ActivitiesAdapter adapter = new ActivitiesAdapter(getApplicationContext(), ac_pname, ac_fname, ac_content, ac_date);
					lv.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					lv.setEmptyView(findViewById(R.id.textView2));
					
			
				}else{
					txt_lvLabel.setVisibility(View.GONE);
					ActivitiesAdapter adapter = new ActivitiesAdapter(getApplicationContext(), ac_pname, ac_fname, ac_content, ac_date);
					lv.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					lv.setEmptyView(findViewById(R.id.textView2));
					
					setProgressBarIndeterminateVisibility(false);
					
					if (json.getInt("errorCode")==1){
						
						Toast.makeText(DashboardMe.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}else{
						
						Toast.makeText(DashboardMe.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + json);
			}
		}
		
	}
	
	class ActivitiesAdapter extends ArrayAdapter<String>{
		Context context;
		List<String> ac_pnameX;
		List<String> ac_fnameX;
		List<String> ac_contentX;
		List<String> ac_dateX;
		//List<Bitmap> ac_avatarX;
		
		ActivitiesAdapter(Context c, List<String> ac_pname, List<String> ac_fname, List<String> ac_content, List<String> ac_date){
			super(c,R.layout.profile_act_row,R.id.txt_act_rowPName, ac_pname);
			this.context = c;
			this.ac_pnameX = ac_pname;
			this.ac_fnameX = ac_fname;
			this.ac_contentX = ac_content;
			this.ac_dateX = ac_date;
			
			
		}
		
		class myViewHolder{
			TextView text_pname, text_fname, text_content, text_date;
			LinearLayout secHead;
			//ImageView image;
			
			public myViewHolder(View view) {
				secHead = (LinearLayout) view.findViewById(R.id.sectionHeader);
				text_pname = (TextView) view.findViewById(R.id.txt_act_rowPName);
				text_fname = (TextView) view.findViewById(R.id.txt_act_rowUser);
				text_content = (TextView) view.findViewById(R.id.txt_act_content);
				text_date = (TextView) view.findViewById(R.id.txt_act_time);
				//image = (ImageView) view.findViewById(R.id.img_act_upic);
				
				
				
				
				
				
			}
			
			
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			View row = convertView;
			myViewHolder holder = null;
			
			if (row == null){
				inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.profile_act_row, parent, false);
				holder = new myViewHolder(row);
				row.setTag(holder);
			}else{
				holder = (myViewHolder) row.getTag();
			}
			if (currentP.equalsIgnoreCase(ac_pnameX.get(0))) {
				holder.secHead.setVisibility(LinearLayout.VISIBLE);
			}
				
				
				if(currentP.equalsIgnoreCase(ac_pnameX.get(position)) && position !=0){
					holder.secHead.setVisibility(LinearLayout.GONE);
				}else {
					currentP = ac_pnameX.get(position);
					holder.secHead.setVisibility(LinearLayout.VISIBLE);
				}
				
			
				holder.text_pname.setText(ac_pnameX.get(position));
				holder.text_fname.setText(ac_fnameX.get(position));
				holder.text_content.setText(ac_contentX.get(position));
				holder.text_date.setText(ac_dateX.get(position));
				
				
				
				
			
			
			return row;
		}
	}
}
