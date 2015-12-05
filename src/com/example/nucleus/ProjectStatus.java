package com.example.nucleus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.nucleus.Project.getProjectInfo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ProjectStatus extends Activity {
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	
	SharedPreferences prefs;
	Integer user_id, workspace_id, ws_user_level_id, p_user_level_id;
	Integer project_id;
	String project_name;
	String project_desc;
	
	TextView txt_projectName, txt_projectCreator, txt_projectDate,
	txt_projectStatus,txt_totalMembers, txt_totalGoals, txt_totalOpenGoals, 
	txt_totalWIPGoals, txt_totalDoneGoals, txt_totalNumTasks, txt_totalCompleted,
	txt_totalCompletedOnTime, txt_totalCompletedLate ;
	
	ProgressBar prog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.project_status);
		//freshSignup = false;
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		
		project_id = getIntent().getIntExtra("project_id", 0);
		Log.e("check pro", ""+project_id);
		
		txt_projectName = (TextView) findViewById(R.id.txt_stat_ProjectName);
		txt_projectCreator = (TextView) findViewById(R.id.txt_stat_ProjectCreator);
		txt_projectDate = (TextView) findViewById(R.id.txt_stat_ProjectDate);
		txt_projectStatus = (TextView) findViewById(R.id.txt_stat_ProjectStatus);
		txt_totalMembers = (TextView) findViewById(R.id.txt_stat_NumberMembers);
		txt_totalGoals = (TextView) findViewById(R.id.txt_stat_NumberGoals);
		txt_totalOpenGoals = (TextView) findViewById(R.id.txt_stat_GoalsOpen);
		txt_totalWIPGoals = (TextView) findViewById(R.id.txt_stat_GoalsWIP);
		txt_totalDoneGoals = (TextView) findViewById(R.id.txt_stat_GoalsDone);
		txt_totalNumTasks = (TextView) findViewById(R.id.txt_stat_TasksTotal);
		txt_totalCompleted = (TextView) findViewById(R.id.txt_stat_TasksCompleted);
		txt_totalCompletedOnTime = (TextView) findViewById(R.id.txt_stat_TasksCompletedOnTime);
		txt_totalCompletedLate = (TextView) findViewById(R.id.txt_stat_TasksCompletedLate);
		
		prog = (ProgressBar) findViewById(R.id.prog_stat);
		
		
		
		txt_totalOpenGoals.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(ProjectStatus.this, GoalHome.class);
				i.putExtra("goToTab", 0);
				startActivity(i);
			}
		});
		txt_totalWIPGoals.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(ProjectStatus.this, GoalHome.class);
				i.putExtra("goToTab", 1);
				startActivity(i);
			}
		});
		txt_totalDoneGoals.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(ProjectStatus.this, GoalHome.class);
				i.putExtra("goToTab", 2);
				startActivity(i);
			}
		});
		
		new NetCheck().execute();
		
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
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
                
                new getProjectStatus().execute();
                
                
               
            }
            else{
            	setProgressBarIndeterminateVisibility(false);
            	
            	Toast.makeText(getBaseContext(), "Unable to connect to the server", Toast.LENGTH_SHORT);
            	
                
                
            }
		}
		
	}
	
	public class getProjectStatus extends AsyncTask<Void, Void, JSONObject>{
		ProgressDialog pdialog;
		
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pdialog = new ProgressDialog(ProjectStatus.this);
			pdialog.setMessage("Loading project status..");
			pdialog.setCancelable(false);
			pdialog.setIndeterminate(false);
			pdialog.show();
			//setProgressBarIndeterminateVisibility(true);
			
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			userFunctions uf = new userFunctions();
			JSONObject json = uf.getProjectStatus( project_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					//setProgressBarIndeterminateVisibility(false);
					pdialog.dismiss();
					
					txt_projectName.setText(json.getString("txt_projectName"));
					txt_projectCreator.setText("Created by "+json.getString("txt_projectCreator"));
					txt_projectDate.setText("Created on "+json.getString("txt_projectDate"));
					txt_projectStatus.setText(""+json.getString("txt_projectStatus"));
					txt_totalMembers.setText(""+json.getInt("txt_totalMembers"));
					txt_totalGoals.setText(""+json.getInt("txt_totalGoals"));
					txt_totalOpenGoals.setText(""+json.getInt("txt_totalOpenGoals"));
					txt_totalWIPGoals.setText(""+json.getInt("txt_totalWIPGoals"));
					txt_totalDoneGoals.setText(""+json.getInt("txt_totalDoneGoals"));
					txt_totalNumTasks.setText(""+json.getInt("txt_totalNumTasks"));
					txt_totalCompleted.setText(""+json.getInt("txt_totalCompleted"));
					txt_totalCompletedOnTime.setText(""+json.getInt("txt_totalCompletedOnTime"));
					txt_totalCompletedLate.setText(""+json.getInt("txt_totalCompletedLate"));
					
					prog.setProgress(json.getInt("progress"));
					
					
//					Toast.makeText(getBaseContext(), "Successfully Updated Workspace Info", Toast.LENGTH_SHORT).show();
//					finish();
			
				}else{
					pdialog.dismiss();
					
					//Toast.makeText(getBaseContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	

}
