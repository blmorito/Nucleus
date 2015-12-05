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

import com.example.nucleus.GoalOpen.NetCheck;
import com.example.nucleus.GoalOpen.getOpenGoals;
import com.example.nucleus.GoalOpen.openGoalsAdapter;
import com.example.nucleus.GoalOpen.startGoal;
import com.example.nucleus.GoalOpen.openGoalsAdapter.myViewHolder;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Notifications extends Activity {
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	SharedPreferences prefs;
	
	int user_id, workspace_id;
	LayoutInflater inflater;
	JSONArray notifications;
	Bitmap bitmap;
	
	//navigateProject
	Boolean gotoProject;
	int gotoPID;
	
	//navigateGoal
	Boolean gotoGoal;
	int gotoGID;
	
	//navigateTask
	Boolean gotoTask;
	int gotoTID;
	
	//navigateDiscussion
	Boolean gotoDisussion;
	int gotoDID;
	
	//navigateFile
	Boolean gotoFile;
	int gotoFID;
	
	ListView lv;
	
	public List<String> project_id = new ArrayList<String>();
	public List<String> project_name = new ArrayList<String>();
	public List<String> type = new ArrayList<String>();
	public List<String> type_id = new ArrayList<String>();
	public List<String> status = new ArrayList<String>();
	public List<String> content = new ArrayList<String>();
	public List<String> ago = new ArrayList<String>();
	
	public List<Bitmap> avatars = new ArrayList<Bitmap>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		setContentView(R.layout.notifications);
		
		prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
		
		user_id = prefs.getInt("user_id", 0);
		workspace_id = prefs.getInt("workspace_id", 0);
		
		lv = (ListView) findViewById(R.id.listViewNotifications);
		
		//navigateProject
		gotoProject = false;
		gotoGoal = false;
		gotoTask = false;
		gotoDisussion = false;
		gotoFile = false;
		
		new NetCheck().execute();
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				TextView txt_project_id = (TextView) view.findViewById(R.id.txt_notifProjectId);
				TextView txt_type = (TextView) view.findViewById(R.id.txt_notifType);
				TextView txt_type_id = (TextView) view.findViewById(R.id.txt_notifTypeId);
				
				if(txt_type.getText().toString().equalsIgnoreCase("Project")){
					gotoProject = true;
					gotoPID = Integer.parseInt(txt_project_id.getText().toString());
					
					new NetCheck().execute();
				}else if (txt_type.getText().toString().equalsIgnoreCase("Goal")) {
					gotoGoal = true;
					gotoPID = Integer.parseInt(txt_project_id.getText().toString());
					gotoGID = Integer.parseInt(txt_type_id.getText().toString());
					
					new NetCheck().execute();
					
				}else if (txt_type.getText().toString().equalsIgnoreCase("Task")) {
					gotoTask = true;
					gotoPID = Integer.parseInt(txt_project_id.getText().toString());
					gotoTID = Integer.parseInt(txt_type_id.getText().toString());
					
					new NetCheck().execute();
					
				}else if (txt_type.getText().toString().equalsIgnoreCase("Discussion")) {
					gotoDisussion = true;
					gotoPID = Integer.parseInt(txt_project_id.getText().toString());
					gotoDID = Integer.parseInt(txt_type_id.getText().toString());
					
					new NetCheck().execute();
				}else {
					gotoFile = true;
					gotoPID = Integer.parseInt(txt_project_id.getText().toString());
					gotoFID = Integer.parseInt(txt_type_id.getText().toString());
					
					new NetCheck().execute();
				}
			}
		});
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
                
				
				if(gotoProject==true){
					gotoProject = false;
					new navigateProject().execute();
				}else if (gotoGoal==true) {
					gotoGoal = false;
					new navigateGoal().execute();
				}else if (gotoTask == true) {
					gotoTask = false;
					new navigateTask().execute();
					
				}else if (gotoDisussion==true) {
					gotoDisussion = false;
					new navigateDiscussion().execute();
				}else if (gotoFile==true) {
					gotoFile = false;
					new navigateFile().execute();
				}else{
					new GetNotifications().execute();
				}
               
            }
            else{
            	getParent().setProgressBarIndeterminateVisibility(false);
            	
            	Toast.makeText(getBaseContext(), "Unable to connect to the network", Toast.LENGTH_SHORT).show();
            	
                
                
            }
		}
		
	}
	
	public class GetNotifications extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
			JSONObject json = uf.getNotifications(user_id, workspace_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					notifications = json.getJSONArray("notifications");
					Notifications.this.project_id.clear();
					Notifications.this.project_name.clear();
					Notifications.this.type.clear();
					Notifications.this.type_id.clear();
					Notifications.this.status.clear();
					Notifications.this.content.clear();
					Notifications.this.ago.clear();
					Notifications.this.avatars.clear();
					
					
					
					for(int i=0; i< notifications.length(); i++){
						JSONObject z = notifications.getJSONObject(i);
						
						String project_id = ""+z.getInt("project_id");
						String project_name = z.getString("project_name");
						String type = z.getString("type");
						String type_id = z.getString("type_id");
						String status = z.getString("status");
						String content = z.getString("content");
						String ago = z.getString("date");
						String avatars = z.getString("avatar");
						
						Notifications.this.project_id.add(project_id);
						Notifications.this.project_name.add(project_name);
						Notifications.this.type.add(type);
						Notifications.this.type_id.add(type_id);
						Notifications.this.status.add(status);
						Notifications.this.content.add(content);
						Notifications.this.ago.add(ago);
						byte[] blobimg = Base64.decode(avatars.getBytes(), 1);
				    	bitmap = BitmapFactory.decodeByteArray(blobimg, 0, blobimg.length);
						
				    	Notifications.this.avatars.add(bitmap);
						
						
						
						
						
						
						
						
					}
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							
							//wsShowMembers.setVisibility(LinearLayout.VISIBLE);
							//loader.setVisibility(LinearLayout.GONE);
							NotificationAdapter adapter = new NotificationAdapter(getApplicationContext(), project_id, project_name, type, type_id,status, content, ago, avatars);
							lv.setAdapter(adapter);
							lv.setEmptyView(findViewById(R.id.no_notifications));
							adapter.notifyDataSetChanged();
							
						}
					});
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					Notifications.this.project_id.clear();
					Notifications.this.project_name.clear();
					Notifications.this.type.clear();
					Notifications.this.type_id.clear();
					Notifications.this.status.clear();
					Notifications.this.content.clear();
					Notifications.this.ago.clear();
					Notifications.this.avatars.clear();
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							
							NotificationAdapter adapter = new NotificationAdapter(getApplicationContext(), project_id, project_name, type, type_id,status, content, ago, avatars);
							lv.setAdapter(adapter);
							lv.setEmptyView(findViewById(R.id.no_notifications));
							adapter.notifyDataSetChanged();
							
						}
					});
					
					//Toast.makeText(getBaseContext(), "Couldnt retrieve information", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	
	public class navigateProject extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
			JSONObject json = uf.navigateProject(user_id, gotoPID);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					SharedPreferences.Editor editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
		            
		            editor.putInt("p_user_level_id", json.getInt("p_user_level_id"));
		            editor.putInt("project_id", gotoPID);
		            editor.commit();
		            
		            Intent i = new Intent(Notifications.this, Project.class);
		            i.putExtra("project_id", gotoPID);
		            i.putExtra("project_name", json.getString("project_name"));
		            
		            startActivity(i);
					
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					AlertDialog.Builder adb = new AlertDialog.Builder(Notifications.this);
					adb.setTitle("Error");
					adb.setMessage("The project is no longer existing");
					adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					});
					
					adb.show();
					
					
					//Toast.makeText(getBaseContext(), "Couldnt retrieve information", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	
	public class navigateDiscussion extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
			JSONObject json = uf.navigateDiscussion(user_id, gotoPID, gotoDID);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					SharedPreferences.Editor editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
		            
		            editor.putInt("p_user_level_id", json.getInt("p_user_level_id"));
		            editor.putInt("project_id", gotoPID);
		            editor.commit();
		            
//		            txt_author.setText(extras.getString("discuss_author"));
//					txt_subject.setText(extras.getString("discuss_subject"));
//					txt_body.setText(extras.getString("discuss_body"));
//					discussion_id = Integer.parseInt(extras.getString("discuss_id"));
		            
		            
		            Intent i = new Intent(Notifications.this, DiscussionView.class);
		            i.putExtra("discuss_author", json.getString("discussion_author"));
		            i.putExtra("discuss_id", json.getString("discussion_id"));
		            i.putExtra("discuss_body", json.getString("body"));
		            i.putExtra("discuss_subject", json.getString("subject"));
		            
		            
		           
		            startActivity(i);
					
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					AlertDialog.Builder adb = new AlertDialog.Builder(Notifications.this);
					adb.setTitle("Error");
					adb.setMessage("The discussion is no longer existing");
					adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					});
					
					adb.show();
					
					
					//Toast.makeText(getBaseContext(), "Couldnt retrieve information", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	
	public class navigateFile extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
			JSONObject json = uf.navigateProject(user_id, gotoPID);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					SharedPreferences.Editor editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
		            
		            editor.putInt("p_user_level_id", json.getInt("p_user_level_id"));
		            editor.putInt("project_id", gotoPID);
		            editor.commit();
		            
//		            txt_author.setText(extras.getString("discuss_author"));
//					txt_subject.setText(extras.getString("discuss_subject"));
//					txt_body.setText(extras.getString("discuss_body"));
//					discussion_id = Integer.parseInt(extras.getString("discuss_id"));
		            
		            
		            Intent i = new Intent(Notifications.this, Files.class);
		          
		            
		           
		            startActivity(i);
					
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					AlertDialog.Builder adb = new AlertDialog.Builder(Notifications.this);
					adb.setTitle("Error");
					adb.setMessage("The discussion is no longer existing");
					adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					});
					
					adb.show();
					
					
					//Toast.makeText(getBaseContext(), "Couldnt retrieve information", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	public class navigateGoal extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
			JSONObject json = uf.navigateGoal(user_id, gotoPID, gotoGID);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					SharedPreferences.Editor editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
		            
		            editor.putInt("p_user_level_id", json.getInt("p_user_level_id"));
		            editor.putInt("project_id", gotoPID);
		            editor.commit();
		            
		            Intent i = new Intent(Notifications.this, GoalView.class);
					i.putExtra("goal_id", gotoGID);
					i.putExtra("goal_name", json.getString("goal_name"));
					i.putExtra("goal_creator", json.getString("goal_creator"));
					i.putExtra("goal_creator_id", json.getInt("goal_creator_id"));
					i.putExtra("goal_date", json.getString("goal_date"));
					
					startActivity(i);
					
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					AlertDialog.Builder adb = new AlertDialog.Builder(Notifications.this);
					adb.setTitle("Error");
					adb.setMessage("The goal is no longer existing");
					adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					});
					
					adb.show();
					
					
					//Toast.makeText(getBaseContext(), "Couldnt retrieve information", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	
	public class navigateTask extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
			JSONObject json = uf.navigateTask(user_id, gotoPID, gotoTID);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					SharedPreferences.Editor editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
		            
		            editor.putInt("p_user_level_id", json.getInt("p_user_level_id"));
		            editor.putInt("project_id", gotoPID);
		            editor.commit();
		            
		            Intent i = new Intent(Notifications.this, TaskView.class);
		            i.putExtra("task_id", gotoTID);
		            i.putExtra("task_date", json.getString("date_due"));
		            i.putExtra("task_name", json.getString("task_name"));
		            i.putExtra("task_status", json.getString("task_status"));
		            i.putExtra("task_isLate", json.getString("isLate"));
		            i.putExtra("task_assignee", json.getString("task_assignee"));
		            
		            
		            startActivity(i);
					
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					AlertDialog.Builder adb = new AlertDialog.Builder(Notifications.this);
					adb.setTitle("Error");
					adb.setMessage("The task is no longer existing");
					adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					});
					
					adb.show();
					
					
					//Toast.makeText(getBaseContext(), "Couldnt retrieve information", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	
	class NotificationAdapter extends ArrayAdapter<String>{
		

		Context context;
		List<String> project_idX;
		List<String> project_nameX;
		List<String> typeX;
		List<String> type_idX;
		List<String> statusX;
		List<String> contentX;
		List<String> agoX;
		List<Bitmap> avatarsX;
		
		
		NotificationAdapter(Context c, List<String> project_id, List<String> project_name, 
				List<String> type, List<String> type_id,List<String> status, List<String> content, List<String> ago, List<Bitmap> avatars){
			super(c,R.layout.notifications_row,R.id.txt_notifProjectId, project_id);
			this.context = c;
			this.project_idX = project_id;
			this.project_nameX = project_name;
			this.typeX = type;
			this.type_idX = type_id;
			this.statusX = status;
			this.contentX = content;
			this.agoX = ago;
			this.avatarsX = avatars;
			
		}
		
		class myViewHolder{
			TextView txt_projectId, txt_projectName, txt_type,txt_type_id, txt_status, txt_content, txt_ago;
			
			ImageView av;
			
			
			public myViewHolder(View view) {
				txt_projectId = (TextView) view.findViewById(R.id.txt_notifProjectId);
				txt_projectName = (TextView) view.findViewById(R.id.txt_notifProjectName);
				txt_type = (TextView) view.findViewById(R.id.txt_notifType);
				txt_type_id = (TextView) view.findViewById(R.id.txt_notifTypeId);
				txt_status = (TextView) view.findViewById(R.id.txt_notifStatus);
				txt_content = (TextView) view.findViewById(R.id.txt_notifContent);
				txt_ago = (TextView) view.findViewById(R.id.txt_notifAgo);
				av = (ImageView) view.findViewById(R.id.imageView1);
				
				
				
			}
			
			
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			View row = convertView;
			myViewHolder holder = null;
			
			if (row == null){
				inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.notifications_row, parent, false);
				holder = new myViewHolder(row);
				row.setTag(holder);
			}else{
				holder = (myViewHolder) row.getTag();
			}
				//holder.prog.setVisibility(View.GONE);
			
				holder.txt_projectId.setText(project_idX.get(position));
				holder.txt_projectName.setText(project_nameX.get(position));
				holder.txt_type.setText(typeX.get(position));
				holder.txt_type_id.setText(type_idX.get(position));
				holder.txt_status.setText(statusX.get(position));
				holder.txt_content.setText(Html.fromHtml(contentX.get(position)));
				holder.txt_ago.setText(Html.fromHtml(agoX.get(position)));
				
				holder.av.setImageBitmap(getRoundedShape(avatarsX.get(position)));
				
				
				if(holder.txt_status.getText().toString().equalsIgnoreCase("Unread")){
					row.setBackgroundColor(Color.parseColor("#e5edf4"));
				}else{
					row.setBackgroundColor(Color.WHITE);
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
