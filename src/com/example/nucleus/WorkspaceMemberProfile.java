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

import com.example.nucleus.DashboardMe.getProfileData;
import com.example.nucleus.Discussion.discussionsAdapter;
import com.example.nucleus.Discussion.discussionsAdapter.myViewHolder;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class WorkspaceMemberProfile extends Activity {
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	SharedPreferences prefs;
	
	ImageView img_profilePic;
	Button btn_makeAdmin, btn_removeAdmin;
	TextView txt_wsmemName, txt_wsmemEmail, txt_wsmemRole;
	ListView listview1;
	Bitmap bitmap;
	int user_id, workspace_id,profile_id, ws_user_level_id;
	Boolean toMake, toRemove;
	
	//aaaaaaaaaa
	JSONArray projects;
	LayoutInflater inflater;
	public List<String> project_id = new ArrayList<String>();
	public List<String> project_name = new ArrayList<String>();
	public List<String> project_leader = new ArrayList<String>();
	public List<String> project_status = new ArrayList<String>();
	public List<String> project_progress = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.wsmember_profile);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		
		toMake = false;
		toRemove = false;
		
		
		
		prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
		user_id = prefs.getInt("user_id", 0);
		workspace_id = prefs.getInt("workspace_id", 0);
		ws_user_level_id = prefs.getInt("ws_user_level_id", 0);
		profile_id = Integer.parseInt(getIntent().getStringExtra(("profile_id")));
		Log.e("profz",""+profile_id);
		txt_wsmemName = (TextView) findViewById(R.id.txt_wsmemprofName);
		txt_wsmemEmail = (TextView) findViewById(R.id.txt_wsmemprofEmail);
		txt_wsmemRole = (TextView) findViewById(R.id.txt_wsmemprofRole);
		img_profilePic = (ImageView) findViewById(R.id.img_wsmemprofPic);
		btn_makeAdmin = (Button) findViewById(R.id.btn_memprofMakeAdmin);
		btn_removeAdmin = (Button) findViewById(R.id.btn_memprofRemoveAdmin);
		listview1 = (ListView) findViewById(R.id.listviewWsMemProj);
		
		btn_makeAdmin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder adb = new AlertDialog.Builder(WorkspaceMemberProfile.this);
				adb.setTitle("Make Admin");
				adb.setMessage("Are you sure you want to grant admin privilege to this user?");
				adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						toMake = true;
						new NetCheck().execute();
					}
				});
				adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				adb.show();
			}
		});
		
		btn_removeAdmin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder adb = new AlertDialog.Builder(WorkspaceMemberProfile.this);
				adb.setTitle("Revoke Admin privilege");
				adb.setMessage("Are you sure you want to revoke admin privilege to this user?");
				adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						toRemove = true;
						new NetCheck().execute();
					}
				});
				adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				adb.show();
			}
		});
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
                
                
				if(toMake==true){
					toMake=false;
					new makeAdmin().execute();
				}else if (toRemove==true) {
					toRemove = false;
					new removeAdmin().execute();
					
				}else{
					new getProfileData().execute();
					
				}
                //new loginAccount().execute();
                //Toast.makeText(getBaseContext(), "Connection Active", Toast.LENGTH_SHORT).show();
                
            }
            else{
            	setProgressBarIndeterminateVisibility(false);
            	
            	
            	
                
                
            }
		}
		
	}
	public class makeAdmin extends AsyncTask<Void, Void, JSONObject>{
		
		ProgressDialog pDialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			//setProgressBarIndeterminateVisibility(true);
			
			pDialog = new ProgressDialog(WorkspaceMemberProfile.this);
			pDialog.setMessage("Updating member..");
			pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.e("pro",""+profile_id);
			userFunctions uf = new userFunctions();
			JSONObject json = uf.makeAdmin(profile_id, workspace_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					//setProgressBarIndeterminateVisibility(false);
					pDialog.dismiss();
					Toast.makeText(getBaseContext(), "Member Successfully updated", Toast.LENGTH_SHORT).show();
			    	
					
			    	//layoutProfile.setVisibility(LinearLayout.VISIBLE);
			
				}else{
					//setProgressBarIndeterminateVisibility(false);
					pDialog.dismiss();
					
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	
	public class removeAdmin extends AsyncTask<Void, Void, JSONObject>{
		
		ProgressDialog pDialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			//setProgressBarIndeterminateVisibility(true);
			
			pDialog = new ProgressDialog(WorkspaceMemberProfile.this);
			pDialog.setMessage("Updating member..");
			pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.e("pro",""+profile_id);
			userFunctions uf = new userFunctions();
			JSONObject json = uf.removeAdmin(profile_id, workspace_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					//setProgressBarIndeterminateVisibility(false);
					pDialog.dismiss();
					Toast.makeText(getBaseContext(), "Member Successfully updated", Toast.LENGTH_SHORT).show();
			    	
					
			    	//layoutProfile.setVisibility(LinearLayout.VISIBLE);
			
				}else{
					//setProgressBarIndeterminateVisibility(false);
					pDialog.dismiss();
					
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	
	public class getProfileData extends AsyncTask<Void, Void, JSONObject>{
		
		ProgressDialog pDialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			//setProgressBarIndeterminateVisibility(true);
			
			pDialog = new ProgressDialog(WorkspaceMemberProfile.this);
			pDialog.setMessage("Loading profile..");
			pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.e("pro",""+profile_id);
			userFunctions uf = new userFunctions();
			JSONObject json = uf.getWsProfileData(profile_id, workspace_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					//setProgressBarIndeterminateVisibility(false);
					pDialog.dismiss();
					txt_wsmemName.setText(json.getString("full_name"));
					txt_wsmemEmail.setText(json.getString("email"));
					
					int tempx = json.getInt("user_level_id");
					if(tempx == 1){
						txt_wsmemRole.setText("Workspace Creator");
						btn_makeAdmin.setVisibility(View.GONE);
						btn_removeAdmin.setVisibility(View.GONE);
						
					}else if (tempx==2) {
						txt_wsmemRole.setText("Workspace Administrator");
						if(user_id==profile_id){
							btn_makeAdmin.setVisibility(View.GONE);
							btn_removeAdmin.setVisibility(View.GONE);
						}else{
							if(ws_user_level_id!=1){
								btn_makeAdmin.setVisibility(View.GONE);
								btn_removeAdmin.setVisibility(View.GONE);
							}else{
								btn_makeAdmin.setVisibility(View.GONE);
							}
						}
					}else{
						txt_wsmemRole.setText("Workspace Member");
						
						if(user_id == profile_id){
							btn_makeAdmin.setVisibility(View.GONE);
							btn_removeAdmin.setVisibility(View.GONE);
						}else{
							if(ws_user_level_id!=1){
								btn_makeAdmin.setVisibility(View.GONE);
								btn_removeAdmin.setVisibility(View.GONE);
							}else{
								btn_removeAdmin.setVisibility(View.GONE);
							}
						}
						
					}
					
					String bm = json.getString("avatar");
					byte[] blobimg = Base64.decode(bm.getBytes(), 1);
			    	Bitmap bitmap = BitmapFactory.decodeByteArray(blobimg, 0, blobimg.length);
			    	
			    	img_profilePic.setImageBitmap(getRoundedShape(bitmap));
			    	
			    	project_id.clear();
			    	project_leader.clear();
			    	project_name.clear();
			    	project_status.clear();
			    	project_progress.clear();
			    	
			    	projects = json.getJSONArray("projects");
			    	for(int i=0; i<projects.length(); i++){
			    		
			    		JSONObject z = projects.getJSONObject(i);
			    		project_id.add(""+z.getInt("project_id"));
			    		project_name.add(z.getString("project_name"));
			    		project_leader.add("Leader: "+z.getString("project_leader"));
			    		project_status.add(z.getString("project_status"));
			    		project_progress.add(""+z.getInt("progress"));
			    		
			    	}
			    	
			    	projectsAdapter adapter = new projectsAdapter(getApplicationContext(), project_id, project_name, project_leader, project_status,project_progress);
					listview1.setAdapter(adapter);
					listview1.setEmptyView(findViewById(R.id.no_projects_involved));
					adapter.notifyDataSetChanged();
			    	
			    	
					
			    	//layoutProfile.setVisibility(LinearLayout.VISIBLE);
			
				}else{
					//setProgressBarIndeterminateVisibility(false);
					pDialog.dismiss();
					
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	
	class projectsAdapter extends ArrayAdapter<String>{
		Context context;
		List<String> project_idX;
		List<String> project_nameX;
		List<String> project_leaderX;
		List<String> project_statusX;
		List<String> project_progressX;
		
		
		projectsAdapter(Context c, List<String> project_id, List<String> project_name, List<String> project_leader, List<String> project_status,List<String> project_progress){
			super(c,R.layout.wsmember_profile_row,R.id.txt_row_wsmemberProjId, project_id);
			this.context = c;
			this.project_idX = project_id;
			this.project_nameX = project_name;
			this.project_leaderX = project_leader;
			this.project_statusX = project_status;
			this.project_progressX = project_progress;
			
		}
		
		class myViewHolder{
			TextView txt_id, txt_name, txt_leader, txt_status;
			ProgressBar prog;
			
			
			
			public myViewHolder(View view) {
				txt_id = (TextView) view.findViewById(R.id.txt_row_wsmemberProjId);
				txt_name = (TextView) view.findViewById(R.id.txt_row_wsmemberProjectName);
				txt_leader = (TextView) view.findViewById(R.id.txt_row_wsmemberProjectLeader);
				txt_status = (TextView) view.findViewById(R.id.txt_row_wsmemberStatus);
				
				prog = (ProgressBar) view.findViewById(R.id.prog_wsmember);
				
			
			}
			
			
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			View row = convertView;
			myViewHolder holder = null;
			
			if (row == null){
				inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.wsmember_profile_row, parent, false);
				holder = new myViewHolder(row);
				row.setTag(holder);
			}else{
				holder = (myViewHolder) row.getTag();
			}
			
			
				holder.txt_id.setText(project_idX.get(position));
				holder.txt_name.setText(project_nameX.get(position));
				holder.txt_leader.setText(project_leaderX.get(position));
				holder.txt_status.setText(project_statusX.get(position));
				holder.prog.setProgress(Integer.parseInt(project_progressX.get(position)));
				
				//holder.image.setImageBitmap(getRoundedShape(avatarsX.get(position)));
				
				
				
			
			
			return row;
		}
	}

}
