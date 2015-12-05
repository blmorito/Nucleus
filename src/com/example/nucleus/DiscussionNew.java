package com.example.nucleus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.nucleus.ProjectInvite.getProjectMembersEmails;
import com.example.nucleus.ProjectInvite.sendProjectInvite;

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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class DiscussionNew extends Activity {
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	SharedPreferences prefs;
	Integer user_id, workspace_id, project_id;
	
	EditText edt_Subject, edt_Body;
	Boolean toPost;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.discussion_new);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
		toPost = false;
		user_id = prefs.getInt("user_id", 0);
		workspace_id = prefs.getInt("workspace_id", 0);
		project_id = prefs.getInt("project_id", 0);
		
		edt_Subject = (EditText) findViewById(R.id.edt_discSub);
		edt_Body = (EditText) findViewById(R.id.edt_discMsg);
		
		edt_Subject.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edt_Subject.setError(null);
			}
		});
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_discussion, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		
		case R.id.action_post_discussion:
			if(edt_Subject.getText().toString().equals("")){
				edt_Subject.setError("You must put a subject for this discussion");
			}else{
				toPost=true;
				new NetCheck().execute();
			}
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
                
                
				
                
                if (toPost==true){
                	toPost = false;
                	new postDiscussion().execute();
                	//new sendProjectInvite().execute();
                }else{
                	
                	//new getProjectMembersEmails().execute();
                }
              
               
                
                //new loginAccount().execute();
                //Toast.makeText(getBaseContext(), "Connection Active", Toast.LENGTH_SHORT).show();
                
            }
            else{
            	setProgressBarIndeterminateVisibility(false);


            	
            	
            	Toast.makeText(getBaseContext(), "Couldn't connect to the network", Toast.LENGTH_SHORT).show();
                
                
            }
		}
		
	}
	
	public class postDiscussion extends AsyncTask<Void, Void, JSONObject>{
		String subject, body;
		ProgressDialog pDialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			pDialog = new ProgressDialog(DiscussionNew.this);
            
            pDialog.setMessage("Posting Discussion..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            
            subject = edt_Subject.getText().toString();
            body = edt_Body.getText().toString();
			
            Log.e("hmmm",subject + " | "+body);
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			userFunctions uf = new userFunctions();
			JSONObject json = uf.postDiscussion(user_id, project_id, subject, body);
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
					startActivity(new Intent(DiscussionNew.this, Discussion.class));
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
