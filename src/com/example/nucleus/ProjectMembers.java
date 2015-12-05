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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProjectMembers extends Activity {
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	
	SharedPreferences prefs;
	Integer user_id, workspace_id, project_id, p_user_level_id;
	ListView lv_pmembers;
	
	LayoutInflater inflater;
	Bitmap bitmap;
	JSONArray members;
	
	public List<String> pm_id = new ArrayList<String>();
	public List<String> pm_name = new ArrayList<String>();
	public List<String> pm_date = new ArrayList<String>();
	public List<String> pm_role = new ArrayList<String>();
	public List<Bitmap> avatars = new ArrayList<Bitmap>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.project_members);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		
		prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
		
		user_id =  prefs.getInt("user_id", 0);
		workspace_id = prefs.getInt("workspace_id", 0);
		project_id = prefs.getInt("project_id", 0);
		p_user_level_id = prefs.getInt("p_user_level_id",0);
		
		lv_pmembers = (ListView) findViewById(R.id.lv_projMembers);
		
		new NetCheck().execute();
		
		lv_pmembers.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				TextView x = (TextView) view.findViewById(R.id.txt_pmemId);
				TextView y = (TextView) view.findViewById(R.id.txt_pmemName);
				Intent i = new Intent(ProjectMembers.this, DashboardMe.class);
				i.putExtra("user_id", x.getText().toString());
				i.putExtra("profile_name", y.getText().toString());
				startActivity(i);
			}
		});
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.p_members, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		
		case R.id.action_pmem_refresh:
			new NetCheck().execute();
			return true;
			
		case R.id.action_pmem_invite:
			startActivity(new Intent(ProjectMembers.this, ProjectInvite.class));
			return true;
			
		
		case R.id.action_pmem_showinvited:
			startActivity(new Intent(ProjectMembers.this, ProjectMembersPending.class));
			return true;
		
			
		default:
			return super.onOptionsItemSelected(item);
		
		}
	}
	
	
	
	public class NetCheck extends AsyncTask<Void, Void, Boolean>{
		
		
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
                
                
              
                
               new getProjectMembers().execute();
            }
            else{
            	setProgressBarIndeterminateVisibility(false);
            	
            	Toast.makeText(getBaseContext(), "Unable to connect to the network", Toast.LENGTH_SHORT).show();
            	
                
                
            }
		}
		
	}
	
	public class getProjectMembers extends AsyncTask<Void, Void, JSONObject>{
		
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			//Log.e("aaa",""+ws_id);
			userFunctions uf = new userFunctions();
			JSONObject json = uf.getProjectMembers(project_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					members = json.getJSONArray("members");
					ProjectMembers.this.pm_id.clear();
					ProjectMembers.this.pm_name.clear();
					ProjectMembers.this.pm_role.clear();
					ProjectMembers.this.pm_date.clear();
					ProjectMembers.this.avatars.clear();
					
					for(int i=0; i< members.length(); i++){
						JSONObject z = members.getJSONObject(i);
						
						String user_id = ""+z.getInt("user_id");
						String full_name = z.getString("full_name");
						String date = z.getString("date");
						String role = z.getString("role");
						
						String avatar_path = z.getString("avatar");
						
						ProjectMembers.this.pm_id.add(user_id);
						ProjectMembers.this.pm_name.add(full_name);
						ProjectMembers.this.pm_date.add(date);
						ProjectMembers.this.pm_role.add(role);
						
						
						
						byte[] blobimg = Base64.decode(avatar_path.getBytes(), 1);
				    	bitmap = BitmapFactory.decodeByteArray(blobimg, 0, blobimg.length);
						
				    	
				    	ProjectMembers.this.avatars.add(bitmap);
						
					}
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							
							//wsShowMembers.setVisibility(LinearLayout.VISIBLE);
							//loader.setVisibility(LinearLayout.GONE);
							projectMembersAdapter adapter = new projectMembersAdapter(getApplicationContext(), pm_id, pm_name, pm_date, pm_role, avatars);
							lv_pmembers.setAdapter(adapter);
							adapter.notifyDataSetChanged();
							
						}
					});
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							
							//wsShowMembers.setVisibility(LinearLayout.GONE);
							//loader.setVisibility(LinearLayout.GONE);
							
						}
					});
					
					Toast.makeText(getBaseContext(), "Couldnt retrieve information", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	
	class projectMembersAdapter extends ArrayAdapter<String>{
		Context context;
		List<String> pm_idX;
		List<String> pm_nameX;
		List<String> pm_dateX;
		List<String> pm_roleX;
		List<Bitmap> avatarsX;
		
		projectMembersAdapter(Context c, List<String> pm_id, List<String> pm_name, List<String> pm_date, List<String> pm_role, List<Bitmap> avatars){
			super(c,R.layout.project_mem_row,R.id.txt_pmemId, pm_id);
			this.context = c;
			this.pm_idX = pm_id;
			this.pm_nameX = pm_name;
			this.pm_dateX = pm_date;
			this.pm_roleX = pm_role;
			
			this.avatarsX = avatars;
		}
		
		class myViewHolder{
			TextView text_userId, text_fullName, text_date, text_role;
			
			ImageView image;
			
			public myViewHolder(View view) {
				text_userId = (TextView) view.findViewById(R.id.txt_pmemId);
				text_fullName = (TextView) view.findViewById(R.id.txt_pmemName);
				text_date = (TextView) view.findViewById(R.id.txt_pmemSince);
				text_role = (TextView) view.findViewById(R.id.txt_pmemRole);
				
				
				
				
				
				image = (ImageView) view.findViewById(R.id.img_pmemPic);
				
			}
			
			
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			View row = convertView;
			myViewHolder holder = null;
			
			if (row == null){
				inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.project_mem_row, parent, false);
				holder = new myViewHolder(row);
				row.setTag(holder);
			}else{
				holder = (myViewHolder) row.getTag();
			}
			
			
				holder.text_userId.setText(pm_idX.get(position));
				holder.text_fullName.setText(pm_nameX.get(position));
				holder.text_date.setText(pm_dateX.get(position));
				holder.text_role.setText(pm_roleX.get(position));
				
				holder.image.setImageBitmap(getRoundedShape(avatarsX.get(position)));
				
				holder.image.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent i = new Intent(ProjectMembers.this, DashboardMe.class);
						i.putExtra("user_id", pm_idX.get(position));
						i.putExtra("profile_name",pm_nameX.get(position));
						startActivity(i);
					}
				});
				
			
			
			return row;
		}
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
}
