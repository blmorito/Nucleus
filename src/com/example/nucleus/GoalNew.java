package com.example.nucleus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.nucleus.DiscussionNew.postDiscussion;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GoalNew extends Activity {
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	SharedPreferences prefs;
	Integer user_id, workspace_id, project_id;
	
	EditText edt_goal_name, edt_goal_desc;
	Button btn_addGoal;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.goal_new);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
		
		user_id = prefs.getInt("user_id", 0);
		project_id = prefs.getInt("project_id", 0);
		
		edt_goal_name = (EditText) findViewById(R.id.edt_addgoalName);
		edt_goal_desc = (EditText) findViewById(R.id.edt_addgoalDesc);
		btn_addGoal = (Button) findViewById(R.id.btn_addGoal);
		
		edt_goal_name.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edt_goal_name.setError(null);
			}
		});
		btn_addGoal.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(edt_goal_name.getText().toString().trim().equals("")){
					edt_goal_name.setError("A goal name is required");
				}else{
					new addGoal().execute();
					
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
                
                
				
                
               
              
               
                new addGoal().execute();
                //new loginAccount().execute();
                //Toast.makeText(getBaseContext(), "Connection Active", Toast.LENGTH_SHORT).show();
                
            }
            else{
            	setProgressBarIndeterminateVisibility(false);


            	
            	
            	Toast.makeText(getBaseContext(), "Couldn't connect to the network", Toast.LENGTH_SHORT).show();
                
                
            }
		}
		
	}
	
	public class addGoal extends AsyncTask<Void, Void, JSONObject>{
		String goalName, goalDesc;
		ProgressDialog pDialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			pDialog = new ProgressDialog(GoalNew.this);
            
            pDialog.setMessage("Adding goal..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            
            goalName = edt_goal_name.getText().toString().trim();
            goalDesc = edt_goal_desc.getText().toString().trim();
			
            //Log.e("hmmm",subject + " | "+body);
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			userFunctions uf = new userFunctions();
			JSONObject json = uf.addGoal(user_id, project_id, goalName, goalDesc);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			pDialog.dismiss();
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					Toast.makeText(getBaseContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
					
					Intent i = new Intent(GoalNew.this, TaskNew.class);
					i.putExtra("goal_name", edt_goal_name.getText().toString().trim());
					i.putExtra("goal_id", json.getInt("goal_id"));
					startActivity(i);
					finish();

					
					
					
			
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

}
