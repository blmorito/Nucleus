package com.example.nucleus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.nucleus.Dashboard.getWorkspaceName;

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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DbWorkspace extends Activity {
	
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	LinearLayout noConnection;
	LinearLayout wsInformation;
	SharedPreferences prefs;
	
	Integer user_id, workspace_id, ws_user_level_id;
	
	TextView txt_wsName, txt_wsDesc, txt_wsCreator, txt_wsDate, txt_wsNumProjects, txt_wsNumMembers;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.dashboard_ws);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		prefs= getSharedPreferences("MyPref", MODE_PRIVATE);
		//layouts
		noConnection = (LinearLayout) findViewById(R.id.no_connection_layout_ws);
		wsInformation = (LinearLayout) findViewById(R.id.workspace_info);
		
		txt_wsName = (TextView) findViewById(R.id.txt_wsName);
		txt_wsDesc = (TextView) findViewById(R.id.txt_wsDesc);
		txt_wsCreator = (TextView) findViewById(R.id.txt_wsCreator);
		txt_wsDate = (TextView) findViewById(R.id.txt_wsDate);
		txt_wsNumMembers = (TextView) findViewById(R.id.txt_wsNumMembers);
		txt_wsNumProjects = (TextView) findViewById(R.id.txt_wsNumProjects);
		
		Button btn_wsSettings = (Button) findViewById(R.id.btn_infoShowSettings);
		
	
		
		user_id = prefs.getInt("user_id", 0);
		workspace_id = prefs.getInt("workspace_id", 0);
		ws_user_level_id = prefs.getInt("ws_user_level_id", 0);
		
		if(ws_user_level_id==3){
			btn_wsSettings.setVisibility(View.GONE);
			
		}else{
			btn_wsSettings.setVisibility(View.VISIBLE);
		}
		
		new NetCheck().execute();
		
		
		Button btn_showWsMembers = (Button) findViewById(R.id.btn_infoShowMembers);
		btn_showWsMembers.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(DbWorkspace.this, WsShowMembersTLayout.class);
				i.putExtra("ws_id", workspace_id);
				startActivity(i);
			}
		}); 
		
		Button btn_showWsProjects = (Button) findViewById(R.id.btn_infoShowProjects);
		btn_showWsProjects.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(DbWorkspace.this, WsShowProjects.class);
				i.putExtra("ws_id", workspace_id);
				startActivity(i);
			}
		});
		
		Button btn_switchWorkspace = (Button) findViewById(R.id.btn_infoSwitch);
		btn_switchWorkspace.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(DbWorkspace.this, SwitchWorkspace.class));
			}
		});
		
		
		btn_wsSettings.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(DbWorkspace.this, WorkspaceSettings.class);
				i.putExtra("workspace_name", txt_wsName.getText().toString());
				i.putExtra("workspace_desc", txt_wsDesc.getText().toString());
				startActivity(i);
			}
		});
		
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.ws_menu,menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		new NetCheck().execute();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		switch(item.getItemId()){
		
			case R.id.action_refresh:
				new NetCheck().execute();
				return true;
			case R.id.action_invite_workspace:
				startActivity(new Intent(DbWorkspace.this,InviteWorkspace.class));
				return true;
			case R.id.action_create_workspace:
				startActivity(new Intent(DbWorkspace.this, CreateWorkspace.class));
				return true;
			case R.id.action_switch_workspace:
				startActivity(new Intent(DbWorkspace.this, SwitchWorkspace.class));
				return true;	
			default:
				return super.onOptionsItemSelected(item);
				
		}
		
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		if(ws_user_level_id==3){
			menu.findItem(R.id.action_invite_workspace).setVisible(false);
		}else{
			menu.findItem(R.id.action_invite_workspace).setVisible(true);
		}
		
		return super.onPrepareOptionsMenu(menu);
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
                
                
                runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						noConnection.setVisibility(LinearLayout.GONE);
						
					}
				});
                
               new getWorkspaceInfo().execute();
                
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
						wsInformation.setVisibility(LinearLayout.GONE);
					}
				});
            	
                
                
            }
		}
		
	}
	
	public class getWorkspaceInfo extends AsyncTask<Void, Void, JSONObject>{
		
		Integer ws_id;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			ws_id =prefs.getInt("workspace_id", 0);
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			userFunctions uf = new userFunctions();
			JSONObject json = uf.getWSinfo(ws_id, user_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					
					Log.e("nisud","OH");
					setProgressBarIndeterminateVisibility(false);
					 final String ws_name = json.getString("ws_name");
					 final String ws_desc = json.getString("ws_desc");
					 final String ws_date = json.getString("ws_date");
					 final String ws_creator = json.getString("ws_creator");
					 final String memberNum = json.getString("ws_memberNum");
					 
					 
					 final String projectNum = json.getString("ws_projectNum");
					
					 SharedPreferences.Editor editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
	                
	                editor.putInt("ws_user_level_id", json.getInt("ws_user_level_id"));
	                editor.commit();
	                
	                ws_user_level_id = prefs.getInt("ws_user_level_id", 0);
	                
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							
							
							
							// TODO Auto-generated method stub
							txt_wsName.setText(ws_name);
							txt_wsDesc.setText(ws_desc);
							txt_wsDate.setText(ws_date);
							txt_wsCreator.setText(ws_creator);
							txt_wsNumMembers.setText(memberNum);
							
							
							if (projectNum.equalsIgnoreCase("null")){
								txt_wsNumProjects.setText("0");
							}else{
								txt_wsNumProjects.setText(projectNum);
							}
							
							wsInformation.setVisibility(LinearLayout.VISIBLE);
						}
					});
					
					
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					if (json.getInt("errorCode")==1){
						
						Toast.makeText(DbWorkspace.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}else{
						
						Toast.makeText(DbWorkspace.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
}
