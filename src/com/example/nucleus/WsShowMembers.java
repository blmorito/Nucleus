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

import com.example.nucleus.DbWorkspace.NetCheck;

import android.app.ActionBar;
import android.app.Activity;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WsShowMembers extends Activity {
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	
	SharedPreferences prefs;
	Integer ws_id;
	
	LinearLayout noConnection;
	LinearLayout wsShowMembers;
	LinearLayout loader;
	JSONArray members;
	Bitmap bitmap;
	
	public List<String> user_id = new ArrayList<String>();
	public List<String> full_name = new ArrayList<String>();
	public List<String> email = new ArrayList<String>();
	public List<String> role = new ArrayList<String>();
	
	public List<Bitmap> avatars = new ArrayList<Bitmap>();
	
	ListView lv_showMembers;
	LayoutInflater inflater;
	
	EditText edt_searchMembers;
	
	int ws_user_level_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.ws_show_members);
		
		prefs= getSharedPreferences("MyPref", MODE_PRIVATE);
		
		ws_user_level_id = prefs.getInt("ws_user_level_id", 0);
		ws_id = prefs.getInt("workspace_id", 0);
		
		noConnection = (LinearLayout) findViewById(R.id.no_connection_layout_wsmembers);
		wsShowMembers = (LinearLayout) findViewById(R.id.ws_showmembers);
		loader = (LinearLayout) findViewById(R.id.ws_showmembersloader);
		lv_showMembers = (ListView) findViewById(R.id.lv_ws_showMembers);
		
		loadMem();
		
		edt_searchMembers = (EditText) findViewById(R.id.edt_searchMembers);
		edt_searchMembers.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				 List<String> temp_user_id = new ArrayList<String>();
				 List<String> temp_full_name = new ArrayList<String>();
				 List<String> temp_email = new ArrayList<String>();
				 List<String> temp_role = new ArrayList<String>();
				 List<Bitmap> temp_avatars = new ArrayList<Bitmap>();
				 
				 String sampleString = edt_searchMembers.getText().toString().toLowerCase();
				 for(int i=0; i<full_name.size();i++){
					 
					 String fullname = full_name.get(i).toString().toLowerCase();
					 String emailz = email.get(i).toString().toLowerCase();
					 
					 if (fullname.matches(".*"+sampleString+".*") || emailz.matches(".*"+sampleString+".*") ){
						 temp_user_id.add(user_id.get(i));
						 temp_full_name.add(full_name.get(i));
						 temp_email.add(email.get(i));
						 temp_role.add(role.get(i));
						 temp_avatars.add(avatars.get(i));
					 }
					 wsMembersAdapter adapter = new wsMembersAdapter(getApplicationContext(), temp_user_id, temp_full_name, temp_email, temp_role, temp_avatars);
					 lv_showMembers.setAdapter(adapter);
					 adapter.notifyDataSetChanged();
				 }
				 
				 if(sampleString.equalsIgnoreCase("")){
					 loadMem();
				 }
				 
				 
			}
		});
		
	}
	
	public void loadMem() {
		// TODO Auto-generated method stub
		new NetCheck().execute();
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
	protected void onResume() {
		// TODO Auto-generated method stub
		loadMem();
		super.onResume();
	}
	

	
	
	public class NetCheck extends AsyncTask<Void, Void, Boolean>{
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					loader.setVisibility(LinearLayout.VISIBLE);
				}
			});
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
						noConnection.setVisibility(LinearLayout.GONE);
						
						
					}
				});
                
               new getWsMembers().execute();
                
                //new loginAccount().execute();
                //Toast.makeText(getBaseContext(), "Connection Active", Toast.LENGTH_SHORT).show();
                
            }
            else{
            	setProgressBarIndeterminateVisibility(false);
            	
            	runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						noConnection.setVisibility(LinearLayout.VISIBLE);
						wsShowMembers.setVisibility(LinearLayout.GONE);
						loader.setVisibility(LinearLayout.GONE);
					}
				});
            	
                
                
            }
		}
		
	}
	
	public class getWsMembers extends AsyncTask<Void, Void, JSONObject>{
		
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.e("aaa",""+ws_id);
			userFunctions uf = new userFunctions();
			JSONObject json = uf.getWsMembers(ws_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					members = json.getJSONArray("members");
					WsShowMembers.this.user_id.clear();
					WsShowMembers.this.full_name.clear();
					WsShowMembers.this.email.clear();
					WsShowMembers.this.role.clear();
					WsShowMembers.this.avatars.clear();
					for(int i=0; i< members.length(); i++){
						JSONObject z = members.getJSONObject(i);
						
						String user_id = ""+z.getInt("user_id");
						String full_name = z.getString("full_name");
						String email = z.getString("email");
						String role = z.getString("user_level");
						
						String avatar_path = z.getString("avatar");
						
						WsShowMembers.this.user_id.add(user_id);
						WsShowMembers.this.full_name.add(full_name);
						WsShowMembers.this.email.add(email);
						WsShowMembers.this.role.add(role);
						
						
						byte[] blobimg = Base64.decode(avatar_path.getBytes(), 1);
				    	bitmap = BitmapFactory.decodeByteArray(blobimg, 0, blobimg.length);
						
				    	WsShowMembers.this.avatars.add(bitmap);
						
					}
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							
							wsShowMembers.setVisibility(LinearLayout.VISIBLE);
							loader.setVisibility(LinearLayout.GONE);
							wsMembersAdapter adapter = new wsMembersAdapter(getApplicationContext(), user_id, full_name, email, role, avatars);
							lv_showMembers.setAdapter(adapter);
							adapter.notifyDataSetChanged();
							
						}
					});
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							
							wsShowMembers.setVisibility(LinearLayout.GONE);
							loader.setVisibility(LinearLayout.GONE);
							
						}
					});
					
					if (json.getInt("errorCode")==1){
						
						Toast.makeText(WsShowMembers.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}else{
						
						Toast.makeText(WsShowMembers.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	
	class wsMembersAdapter extends ArrayAdapter<String>{
		Context context;
		List<String> user_idX;
		List<String> full_nameX;
		List<String> emailX;
		List<String> roleX;
		List<Bitmap> avatarsX;
		
		wsMembersAdapter(Context c, List<String> user_id, List<String> full_name, List<String> email, List<String> role, List<Bitmap> avatars){
			super(c,R.layout.wsmembers_row,R.id.txt_wsmemberid, user_id);
			this.context = c;
			this.user_idX = user_id;
			this.full_nameX = full_name;
			this.emailX = email;
			this.roleX = role;
			
			this.avatarsX = avatars;
		}
		
		class myViewHolder{
			TextView text_userId, text_fullName, text_email, text_role;
			
			ImageView image;
			
			public myViewHolder(View view) {
				text_userId = (TextView) view.findViewById(R.id.txt_wsmemberid);
				text_fullName = (TextView) view.findViewById(R.id.txt_wsMembersNameRow);
				text_email = (TextView) view.findViewById(R.id.txt_wsMembersEmailRow);
				text_role = (TextView) view.findViewById(R.id.txt_wsMembersRoleRow);
				
				
				
				
				
				image = (ImageView) view.findViewById(R.id.img_wsMembersRow);
				
			}
			
			
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			View row = convertView;
			myViewHolder holder = null;
			
			if (row == null){
				inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.wsmembers_row, parent, false);
				holder = new myViewHolder(row);
				row.setTag(holder);
			}else{
				holder = (myViewHolder) row.getTag();
			}
			
			
				holder.text_userId.setText(user_idX.get(position));
				holder.text_fullName.setText(full_nameX.get(position));
				holder.text_email.setText(emailX.get(position));
				holder.text_role.setText(roleX.get(position));
				
				holder.image.setImageBitmap(getRoundedShape(avatarsX.get(position)));
				
				holder.image.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(ws_user_level_id==3 || ws_user_level_id==2){
							
						}else{
							Intent i = new Intent(WsShowMembers.this, WorkspaceMemberProfile.class);
							i.putExtra("profile_id", user_idX.get(position));
							startActivity(i);
							Log.e("ahjhj", "X"+user_idX.get(position)+"X");
						}
//						if(ws_user_level_id==3){
//							Toast.makeText(getBaseContext(), "Can not view profile", Toast.LENGTH_SHORT).show();
//						}else{
//							Toast.makeText(getBaseContext(), "Can view profile", Toast.LENGTH_SHORT).show();
//						}
					}
				});
				
			
			
			return row;
		}
	}
	
}
