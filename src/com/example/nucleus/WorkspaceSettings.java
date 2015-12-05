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

import com.example.nucleus.DbWorkspace.getWorkspaceInfo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WorkspaceSettings extends Activity {
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	SharedPreferences prefs;
	
	Integer user_id, workspace_id, ws_user_level_id, workspace_creator;
	EditText edt_wsName, edt_wsDesc;
	TextView txt_changeCreator;
	Button btn_save, btn_cancel, btn_changeCreator;
	Boolean toUpdate;
	JSONArray wsmembers;
	
	public List<String> wsMemberId = new ArrayList<String>();
	public List<String> wsMemberName = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.settings_workspace);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		prefs= getSharedPreferences("MyPref", MODE_PRIVATE);
		workspace_creator = 0;
		user_id = prefs.getInt("user_id", 0);
		workspace_id = prefs.getInt("workspace_id", 0);
		ws_user_level_id = prefs.getInt("ws_user_level_id", 0);
		Log.e("wslevel", ""+ws_user_level_id);
		toUpdate = false;
		txt_changeCreator = (TextView) findViewById(R.id.txt_changeCreator);
		btn_changeCreator = (Button) findViewById(R.id.btn_changeCreator);
		
		if(ws_user_level_id==1){
			txt_changeCreator.setVisibility(View.VISIBLE);
			btn_changeCreator.setVisibility(View.VISIBLE);
		}else{
			txt_changeCreator.setVisibility(View.GONE);
			btn_changeCreator.setVisibility(View.GONE);
		}
		
		
		edt_wsName = (EditText) findViewById(R.id.edt_setWsName);
		edt_wsDesc = (EditText) findViewById(R.id.edt_setWsDesc);
		
		btn_save = (Button) findViewById(R.id.btn_setSave);
		btn_cancel = (Button) findViewById(R.id.btn_setCancel);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		     
		     edt_wsName.setText(extras.getString("workspace_name"));
		     edt_wsDesc.setText(extras.getString("workspace_desc"));
		     
		     
		}else{
			Toast.makeText(getBaseContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
		}
		
		edt_wsName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edt_wsName.setError(null);
			}
		});
		
		btn_save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				toUpdate = true;
				if (edt_wsName.getText().toString().equals("")){
					edt_wsName.setError("You must put a workspace name.");
				}else{
					new NetCheck().execute();
				}
			}
		});
		
		btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		btn_changeCreator.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder adbz = new AlertDialog.Builder(WorkspaceSettings.this);;
				adbz.setTitle("Change creator");
				adbz.setMessage("Are you sure you want to change workspace ownership?");
				adbz.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						AlertDialog.Builder adb = new AlertDialog.Builder(WorkspaceSettings.this);
						adb.setTitle("Select new project leader");
						ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(WorkspaceSettings.this, android.R.layout.select_dialog_item);
						for(int i = 0; i< wsMemberId.size(); i++){
							arrayAdapter.add(wsMemberName.get(i));
						}
						
						adb.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								String name = wsMemberName.get(which);
								workspace_creator = Integer.parseInt(wsMemberId.get(which));
								btn_changeCreator.setText("Creator: "+name);
								
							}
						});
						adb.show();
					}
				});
				adbz.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				adbz.show();
			}
		});
		
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
                
                
                runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						//noConnection.setVisibility(LinearLayout.GONE);
						
					}
				});
                
                if(toUpdate==true){
                	toUpdate = false;
                	new updateWorkspace().execute();
                }else{
                	new getWorkspaceMembers().execute();
                }
               //new getWorkspaceInfo().execute();
                
                //new loginAccount().execute();
                //Toast.makeText(getBaseContext(), "Connection Active", Toast.LENGTH_SHORT).show();
                
            }
            else{
            	setProgressBarIndeterminateVisibility(false);
            	
            	Toast.makeText(getBaseContext(), "Unable to connect to the server", Toast.LENGTH_SHORT);
            	
                
                
            }
		}
		
	}

	public class updateWorkspace extends AsyncTask<Void, Void, JSONObject>{
		
		String new_wsName, new_wsDesc;
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			new_wsName = edt_wsName.getText().toString();
			new_wsDesc = edt_wsDesc.getText().toString();
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			userFunctions uf = new userFunctions();
			JSONObject json = uf.updateWorkspace(workspace_id, new_wsName, new_wsDesc, workspace_creator);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					
					Toast.makeText(getBaseContext(), "Successfully Updated Workspace Info", Toast.LENGTH_SHORT).show();
					
					finish();
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					Toast.makeText(getBaseContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + json);
			}
		}
		
	}
	
	public class getWorkspaceMembers extends AsyncTask<Void, Void, JSONObject>{
		
		
		
		
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
			JSONObject json = uf.getWorkspaceMembers(workspace_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					workspace_creator = json.getInt("workspace_creator");
					Log.e("ws",""+workspace_creator);
					wsmembers = json.getJSONArray("ws_members");
					
					wsMemberId.clear();
					wsMemberName.clear();
					
					for (int i=0; i<wsmembers.length(); i++){
						JSONObject z = wsmembers.getJSONObject(i);
						int id = z.getInt("user_id");
						String name = z.getString("full_name");
						
						wsMemberId.add(""+id);
						wsMemberName.add(name);
					}
					
					btn_changeCreator.setText("Creator: "+wsMemberName.get(wsMemberId.indexOf(""+workspace_creator)));
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					Toast.makeText(getBaseContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + json);
			}
		}
		
	}
	
	
}
