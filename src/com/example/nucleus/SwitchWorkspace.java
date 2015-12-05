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








import com.example.nucleus.DashboardInvites.NetCheck;
import com.example.nucleus.DashboardInvites.invitesAdapter.myViewHolder;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SwitchWorkspace extends Activity {
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	
	SharedPreferences prefs;
	JSONArray workspaces;
	
	Integer user_id, workspace_id;
	LinearLayout showWs, noConnection;
	ListView lv_switchws;
	LayoutInflater inflater;
	Button btn_addnewws;
	
	Boolean toSwitch;
	Integer toSwitchId;
	
	public List<String> ws_id = new ArrayList<String>();
	public List<String> workspace_name = new ArrayList<String>();
	public List<String> workspace_desc = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.switch_ws);
		toSwitch = false;
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
		user_id = prefs.getInt("user_id", 0);
		workspace_id = prefs.getInt("workspace_id", 0);
		
		showWs = (LinearLayout) findViewById(R.id.layout_showWs);
		noConnection = (LinearLayout) findViewById(R.id.no_connection_layout_switch);
		
		btn_addnewws = (Button) findViewById(R.id.btn_createNewWs);
		lv_switchws = (ListView) findViewById(R.id.listViewws);
		btn_addnewws.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(SwitchWorkspace.this, CreateWorkspace.class));
			}
		});
		new NetCheck().execute();
		
		lv_switchws.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				TextView xid = (TextView) view.findViewById(R.id.txt_switchWsId);
				toSwitch = true;
				toSwitchId = Integer.parseInt(xid.getText().toString());
				new NetCheck().execute();
				
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
                
                
                runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						noConnection.setVisibility(LinearLayout.GONE);
						
						
					}
				});
                
                Log.e("toswitch", toSwitch.toString()+" "+toSwitchId);
                if (toSwitch==true){
                	toSwitch = false;
                	new switchWs().execute();
                }else{
                	new getMyWs().execute();
                }
               //new getWsMembers().execute();
                
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
						showWs.setVisibility(LinearLayout.GONE);
						//loader.setVisibility(LinearLayout.GONE);
					}
				});
            	
                
                
            }
		}
		
	}
	
	public class getMyWs extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
			JSONObject json = uf.getWorkspaces(user_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					workspaces = json.getJSONArray("workspaces");
					SwitchWorkspace.this.ws_id.clear();
					
					SwitchWorkspace.this.workspace_name.clear();
					SwitchWorkspace.this.workspace_desc.clear();
					
					
					for(int i=0; i< workspaces.length(); i++){
						JSONObject x = workspaces.getJSONObject(i);
						
						String ws_id = ""+x.getInt("workspace_id");
						
						String workspace_name = x.getString("workspace_name");
						String workspace_desc = x.getString("workspace_desc");
						
						
						SwitchWorkspace.this.ws_id.add(ws_id);
						
						SwitchWorkspace.this.workspace_name.add(workspace_name);
						SwitchWorkspace.this.workspace_desc.add(workspace_desc);
						
					}
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							
							showWs.setVisibility(LinearLayout.VISIBLE);
							
							workspaceAdapter adapter = new workspaceAdapter(getApplicationContext(), ws_id, workspace_name, workspace_desc);
							lv_switchws.setAdapter(adapter);
							adapter.notifyDataSetChanged();
							
						}
					});
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
//							noInvites.setVisibility(LinearLayout.VISIBLE);
//							showInvites.setVisibility(LinearLayout.GONE);
//							
							
						}
					});
					
					
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + json);
			}
		}
		
	}
	public class switchWs extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
			JSONObject json = uf.switchWorkspace(user_id, toSwitchId);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					SharedPreferences.Editor editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
	                
	                editor.putInt("workspace_id", json.getInt("workspace_id"));
	                editor.putInt("ws_user_level_id", json.getInt("ws_user_level_id"));
	                editor.commit();
	                Intent intent = new Intent(SwitchWorkspace.this, Dashboard.class);
	                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
	                startActivity(intent);
			
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
	
	class workspaceAdapter extends ArrayAdapter<String>{
		Context context;
		List<String> ws_idX;
		List<String> workspace_nameX;
		List<String> workspace_descX;
		
		
		workspaceAdapter(Context c, List<String> ws_id, List<String> workspace_name, List<String> workspace_desc){
			super(c,R.layout.switch_ws_row,R.id.txt_switchWsId, ws_id);
			this.context = c;
			this.ws_idX = ws_id;
			this.workspace_nameX = workspace_name;
			this.workspace_descX = workspace_desc;
		}
		
		class myViewHolder{
			TextView txt_id, txt_wsName, txt_wsDesc;
			ImageView image;
			
			public myViewHolder(View view) {
				txt_id = (TextView) view.findViewById(R.id.txt_switchWsId);
				txt_wsName = (TextView) view.findViewById(R.id.txt_switchWsName);
				txt_wsDesc = (TextView) view.findViewById(R.id.txt_switchWsDesc);
				
				image = (ImageView) view.findViewById(R.id.imageView1ws);
				
			}
			
			
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			View row = convertView;
			myViewHolder holder = null;
			
			if (row == null){
				inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.switch_ws_row, parent, false);
				holder = new myViewHolder(row);
				row.setTag(holder);
			}else{
				holder = (myViewHolder) row.getTag();
			}
			
			
				holder.txt_id.setText(ws_idX.get(position));
				holder.txt_wsName.setText(workspace_nameX.get(position));
				holder.txt_wsDesc.setText(workspace_descX.get(position));
			
			
			return row;
		}
	}

}
