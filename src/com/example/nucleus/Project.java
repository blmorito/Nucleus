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
import com.example.nucleus.Dashboard.ActivitiesAdapter.myViewHolder;
import com.example.nucleus.ProjectSettings.getProjectSetting;
import com.example.nucleus.ProjectSettings.updateProjectSetting;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Project extends Activity {
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	
	SharedPreferences prefs;
	Integer user_id, workspace_id, ws_user_level_id, p_user_level_id;
	Integer project_id;
	String project_name;
	String project_desc;
	
	//for activities
	LayoutInflater inflater;
	ListView lv;
	JSONArray activities;
	Bitmap bitmap;
	public List<Bitmap> ac_avatar = new ArrayList<Bitmap>();
//	public List<String> ac_pname = new ArrayList<String>();
	public List<String> ac_fname = new ArrayList<String>();
	public List<String> ac_content = new ArrayList<String>();
	public List<String> ac_date = new ArrayList<String>();
	
	Boolean freshSignup;
	String fromAct;
	Button btn_tasks, btn_files, btn_discussions, btn_events;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.project);
		freshSignup = false;
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		
		
		
		
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		     
		     project_id = extras.getInt("project_id");
		     project_name = extras.getString("project_name");
		     freshSignup = extras.getBoolean("fromFreshSignUp", false);
		     
		}
		
		
		
		setTitle(project_name);
		lv = (ListView) findViewById(R.id.listView1);
		prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
		user_id = prefs.getInt("user_id", 0);
		workspace_id = prefs.getInt("workspace_id", 0);
		ws_user_level_id = prefs.getInt("ws_user_level_id",0);
		p_user_level_id = prefs.getInt("p_user_level_id",0);
		project_id = prefs.getInt("project_id", 0);
		new NetCheck().execute();
		
		
		invalidateOptionsMenu();
		btn_discussions = (Button) findViewById(R.id.btn_projectDiscussions);
		btn_events = (Button) findViewById(R.id.btn_projectEvents);
		btn_files = (Button) findViewById(R.id.btn_projectFiles);
		btn_tasks = (Button) findViewById(R.id.btn_projectTasks);
		
		btn_discussions.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(Project.this, Discussion.class));
			}
		});
		
		btn_files.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(Project.this, Files.class));
			}
		});
		
		btn_tasks.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(Project.this, GoalHome.class));
			}
		});
		
		btn_events.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Project.this, ProjectStatus.class);
				i.putExtra("project_id", project_id);
				startActivity(i);
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(freshSignup==true){
			startActivity(new Intent(Project.this, Dashboard.class));
			finish();
		}else{
			super.onBackPressed();
			
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.project_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		
		case R.id.project_addGoal:
			startActivity(new Intent(Project.this, GoalNew.class));
			return true;
			
		case R.id.project_people:
			startActivity(new Intent(Project.this, ProjectMembers.class));
			return true;
		
		case R.id.project_postDiscussion:
			startActivity(new Intent(Project.this, DiscussionNew.class));
			return true;
			
		case R.id.project_refresh:
			return true;
			
		case R.id.project_settings:
			startActivity(new Intent(Project.this, ProjectSettings.class));
			return true;
			
		case R.id.project_uploadFile:
			startActivity(new Intent(Project.this, Files.class));
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		
		}
		
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		if(p_user_level_id==3){
			menu.findItem(R.id.project_settings).setVisible(false);
			
		}else{
			menu.findItem(R.id.project_settings).setVisible(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		new NetCheck().execute();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new NetCheck().execute();
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
                
                
                new getProjectInfo().execute();
                
               
            }
            else{
            	setProgressBarIndeterminateVisibility(false);
            	
            	Toast.makeText(getBaseContext(), "Unable to connect to the server", Toast.LENGTH_SHORT);
            	
                
                
            }
		}
		
	}
	
    public class getProjectInfo extends AsyncTask<Void, Void, JSONObject>{
		
		
		
		
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
			JSONObject json = uf.getProjectInfo( project_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
				
					
					project_name = json.getString("project_name");
					project_desc = json.getString("project_desc");
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							setTitle(project_name);
						}
					});
					
					new getProjectActivities().execute();
					
//					Toast.makeText(getBaseContext(), "Successfully Updated Workspace Info", Toast.LENGTH_SHORT).show();
//					finish();
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					Toast.makeText(getBaseContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
    
    public class getProjectActivities extends AsyncTask<Void, Void, JSONObject>{
		ProgressDialog pdialog;
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pdialog = new ProgressDialog(Project.this);
			//setProgressBarIndeterminateVisibility(true);
			pdialog.setMessage("Loading project info..");
			pdialog.setCancelable(false);
			pdialog.setIndeterminate(false);
			pdialog.show();
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			userFunctions uf = new userFunctions();
			JSONObject json = uf.getProjectActivities(project_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					//setProgressBarIndeterminateVisibility(false);
					pdialog.dismiss();
					activities = json.getJSONArray("activities");
					
					ac_avatar.clear();
					//ac_pname.clear();
					ac_fname.clear();
					ac_date.clear();
					ac_content.clear();
					
					
					for (int i = 0; i<activities.length(); i++){
						
						JSONObject z = activities.getJSONObject(i);
						//String pname = z.getString("act_pname");
						String fname = z.getString("act_fname");
						String content = z.getString("act_content");
						String agodate = z.getString("act_date");
						String avatar = z.getString("act_avatar");
						
						//ac_pname.add(pname);
						ac_fname.add(fname);
						ac_content.add(content);
						ac_date.add(agodate);
						
						if (avatar==""){
							
							
					    	ac_avatar.add(null);
						}else{
							byte[] blobimg = Base64.decode(avatar.getBytes(), 1);
					    	bitmap = BitmapFactory.decodeByteArray(blobimg, 0, blobimg.length);
							
					    	ac_avatar.add(bitmap);
						}
						
						
					}
					
					
					
					ActivitiesAdapter adapter = new ActivitiesAdapter(getApplicationContext(), ac_fname, ac_content, ac_date, ac_avatar);
					lv.setAdapter(adapter);
					lv.setEmptyView(findViewById(R.id.no_project_activities));
					adapter.notifyDataSetChanged();
					TextView t = (TextView) findViewById(R.id.txt_recent);
					if (lv.getAdapter().getCount()==0) {
						
						t.setVisibility(View.GONE);
					}else{
						t.setVisibility(View.VISIBLE);
					}
						
					
					
			
				}else{
					//setProgressBarIndeterminateVisibility(false);
					pdialog.dismiss();
					ActivitiesAdapter adapter = new ActivitiesAdapter(getApplicationContext(),  ac_fname, ac_content, ac_date, ac_avatar);
					lv.setAdapter(adapter);
					lv.setEmptyView(findViewById(R.id.no_project_activities));
					
					
					adapter.notifyDataSetChanged();
					TextView t = (TextView) findViewById(R.id.txt_recent);
					if (lv.getAdapter().getCount()==0) {
						
						t.setVisibility(View.GONE);
					}else{
						t.setVisibility(View.VISIBLE);
					}
					
					if (json.getInt("errorCode")==1){
						
						Toast.makeText(Project.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}else{
						
						Toast.makeText(Project.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
    
    class ActivitiesAdapter extends ArrayAdapter<String>{
		Context context;
		//List<String> ac_pnameX;
		List<String> ac_fnameX;
		List<String> ac_contentX;
		List<String> ac_dateX;
		List<Bitmap> ac_avatarX;
		
		ActivitiesAdapter(Context c, List<String> ac_fname, List<String> ac_content, List<String> ac_date, List<Bitmap> ac_avatar){
			super(c,R.layout.projectact_row,R.id.txt_act_rowUser, ac_fname);
			this.context = c;
			//this.ac_pnameX = ac_pname;
			this.ac_fnameX = ac_fname;
			this.ac_contentX = ac_content;
			this.ac_dateX = ac_date;
			
			this.ac_avatarX = ac_avatar;
		}
		
		class myViewHolder{
			TextView text_pname, text_fname, text_content, text_date;
			
			ImageView image;
			
			public myViewHolder(View view) {
				text_pname = (TextView) view.findViewById(R.id.txt_act_rowPName);
				text_fname = (TextView) view.findViewById(R.id.txt_act_rowUser);
				text_content = (TextView) view.findViewById(R.id.txt_act_content);
				text_date = (TextView) view.findViewById(R.id.txt_act_time);
				image = (ImageView) view.findViewById(R.id.img_act_upic);
				
				
				
				
				
				
			}
			
			
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return this.ac_fnameX.size();
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			View row = convertView;
			myViewHolder holder = null;
			
			if (row == null){
				inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.projectact_row, parent, false);
				holder = new myViewHolder(row);
				row.setTag(holder);
			}else{
				holder = (myViewHolder) row.getTag();
			}
			
			
				//holder.text_pname.setText(ac_pnameX.get(position));
				holder.text_fname.setText(ac_fnameX.get(position));
				holder.text_content.setText(ac_contentX.get(position));
				holder.text_date.setText(ac_dateX.get(position));
				
				if(ac_avatarX.get(position)==null){
					Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.logo_icon);
					holder.image.setImageBitmap(getRoundedShape(icon));
				}else{
					holder.image.setVisibility(View.VISIBLE);
					holder.image.setImageBitmap(getRoundedShape(ac_avatarX.get(position)));
				}
				
				
				
			
			
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
