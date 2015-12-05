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


















import com.example.nucleus.WsShowMembers.wsMembersAdapter.myViewHolder;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WsShowProjects extends Activity {
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	
	SharedPreferences prefs;
	Integer user_id, workspace_id, ws_user_level_id;
	
	LinearLayout noConnection;
	LinearLayout wsShowProjects;
	//LinearLayout loader;
	JSONArray projects;
	ListView lv_showProjects;
	LayoutInflater inflater;
	EditText edt_searchProject;
	CheckBox cb_showOnlyInvolved;
	
	Boolean goToProject;
	Integer goToProjectid;
	String goToProjectName;
	
	public List<String> project_id = new ArrayList<String>();
	public List<String> project_name = new ArrayList<String>();
	public List<String> project_desc = new ArrayList<String>();
	public List<String> project_leader = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.ws_show_projects);
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		
		prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
		
		user_id = prefs.getInt("user_id", 0);
		workspace_id = prefs.getInt("workspace_id", 0);
		ws_user_level_id = prefs.getInt("ws_user_level_id",0);
		invalidateOptionsMenu();
		goToProject =false;
		
		
		
		noConnection  = (LinearLayout) findViewById(R.id.no_connection_layout_wsprojects);
		wsShowProjects = (LinearLayout) findViewById(R.id.ws_showprojects);
		//loader = (LinearLayout) findViewById(R.id.ws_showprojectsloader);
		
		lv_showProjects = (ListView) findViewById(R.id.lv_ws_showProjects);
		edt_searchProject = (EditText) findViewById(R.id.edt_searchProjects);
		cb_showOnlyInvolved = (CheckBox) findViewById(R.id.cb_showInvolved);
		
		cb_showOnlyInvolved.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				new NetCheck().execute();
			}
		});
		
		lv_showProjects.setEmptyView(findViewById(R.id.empty_lv));
		
		new NetCheck().execute();
		
		lv_showProjects.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				TextView temp_id = (TextView) view.findViewById(R.id.txt_showProjectId);
				TextView temp_name = (TextView) view.findViewById(R.id.txt_showProjectName);
				
				goToProjectid = Integer.parseInt(temp_id.getText().toString());
				goToProjectName = temp_name.getText().toString();
				
				goToProject = true;
				new NetCheck().execute();
			}
		});
		
		edt_searchProject.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				List<String> temp_p_id = new ArrayList<String>();
			    List<String> temp_p_name = new ArrayList<String>();
			    List<String> temp_p_desc = new ArrayList<String>();
			    List<String> temp_p_lead = new ArrayList<String>();
			    String search = edt_searchProject.getText().toString().trim().toUpperCase();
			    
			    for (int i=0; i<project_id.size(); i++){
			    	String name = project_name.get(i).toString().toUpperCase();
			    	String desc = project_desc.get(i).toString().toUpperCase();
			    	String lead = project_leader.get(i).toString().toUpperCase();
			    	
			    	if(name.matches(".*"+search+".*") || desc.matches(".*"+search+".*") || lead.matches(".*"+search+".*")){
			    		temp_p_id.add(project_id.get(i));
			    		temp_p_name.add(project_name.get(i));
			    		temp_p_desc.add(project_desc.get(i));
			    		temp_p_lead.add(project_leader.get(i));
			    		projectsAdapter adapter = new projectsAdapter(getApplicationContext(), temp_p_id, temp_p_name, temp_p_desc, temp_p_lead); 
			    		lv_showProjects.setAdapter(adapter);
						adapter.notifyDataSetChanged();
			    	}
			    }
			    
			    if(search.equalsIgnoreCase("")){
			    	new NetCheck().execute();
			    }
			}
		});;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.wsprojects_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_ws_createproject:
			startActivity(new Intent(WsShowProjects.this, CreateProject.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
		
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		if(ws_user_level_id==3){
			menu.findItem(R.id.action_ws_createproject).setVisible(false);
		}else{
			menu.findItem(R.id.action_ws_createproject).setVisible(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	public class NetCheck extends AsyncTask<Void, Void, Boolean>{
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					//loader.setVisibility(LinearLayout.VISIBLE);
				}
			});
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
                
                if(goToProject==true){
                	goToProject=false;
                	new goToProject().execute();
                }else{
                	new getProjects().execute();
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
						wsShowProjects.setVisibility(LinearLayout.GONE);
						//loader.setVisibility(LinearLayout.GONE);
					}
				});
            	
                
                
            }
		}
		
	}
	
	public class getProjects extends AsyncTask<Void, Void, JSONObject>{
		
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.e("aaa",""+workspace_id);
			userFunctions uf = new userFunctions();
			JSONObject json;
			//if (cb_showOnlyInvolved.isChecked()){
				 json = uf.getMyProjects(user_id, workspace_id);
			//}else{
				 //json = uf.getAllProjects(workspace_id);
			//}
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					projects = json.getJSONArray("projects");
					WsShowProjects.this.project_id.clear();
					WsShowProjects.this.project_name.clear();
					WsShowProjects.this.project_desc.clear();
					WsShowProjects.this.project_desc.clear();
					WsShowProjects.this.project_leader.clear();
					
					for(int i=0; i< projects.length(); i++){
						JSONObject z = projects.getJSONObject(i);
						
						String project_id = ""+z.getInt("project_id");
						String project_name = z.getString("project_name");
						String project_desc = z.getString("project_desc");
						String project_leader = z.getString("project_leader");
						
						
						
						WsShowProjects.this.project_id.add(project_id);
						WsShowProjects.this.project_name.add(project_name);
						WsShowProjects.this.project_desc.add(project_desc);
						WsShowProjects.this.project_leader.add(project_leader);
						
						
					}
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							
							wsShowProjects.setVisibility(LinearLayout.VISIBLE);
							//loader.setVisibility(LinearLayout.GONE);
							
							projectsAdapter adapter = new projectsAdapter(getApplicationContext(), project_id, project_name, project_desc, project_leader);
							lv_showProjects.setAdapter(adapter);
							adapter.notifyDataSetChanged();
//							wsMembersAdapter adapter = new wsMembersAdapter(getApplicationContext(), user_id, full_name, email, role, avatars);
//							lv_showMembers.setAdapter(adapter);
//							adapter.notifyDataSetChanged();
							
						}
					});
			
				}else{
					setProgressBarIndeterminateVisibility(false);
//					
					Toast.makeText(WsShowProjects.this, json.getString("message"), Toast.LENGTH_SHORT).show();
//					runOnUiThread(new Runnable() {
////						
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							
//							.setVisibility(LinearLayout.GONE);
//							loader.setVisibility(LinearLayout.GONE);
//							
//						}
//					});
					
//					if (json.getInt("errorCode")==1){
//						
//						Toast.makeText(WsShowMembers.this, json.getString("message"), Toast.LENGTH_SHORT).show();
//					}else{
//						
//						Toast.makeText(WsShowMembers.this, json.getString("message"), Toast.LENGTH_SHORT).show();
//					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
  public class goToProject extends AsyncTask<Void, Void, JSONObject>{
		
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.e("aaa",""+goToProjectName);
			userFunctions uf = new userFunctions();
			JSONObject json = uf.goToProject(user_id, goToProjectid);
			
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
	                editor.putInt("project_id", goToProjectid);
	                editor.commit();
	                
	                Intent i = new Intent(WsShowProjects.this, Project.class);
	                i.putExtra("project_id", goToProjectid);
	                i.putExtra("project_name", goToProjectName);
	                
	                startActivity(i);
					
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					Toast.makeText(WsShowProjects.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					
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
		List<String> project_descX;
		List<String> project_leaderX;
		
		
		
		projectsAdapter(Context c, List<String> project_id, List<String> project_name, List<String> project_desc, List<String> project_leader){
			super(c,R.layout.ws_projectsrow,R.id.txt_showProjectId, project_id);
			this.context = c;
			this.project_idX = project_id;
			this.project_nameX = project_name;
			this.project_descX = project_desc;
			this.project_leaderX = project_leader;
			
		}
		
		class myViewHolder{
			TextView txt_id, txt_name, txt_desc,txt_lead;
			
			ImageView image;
			
			public myViewHolder(View view) {
				txt_id = (TextView) view.findViewById(R.id.txt_showProjectId);
				txt_name = (TextView) view.findViewById(R.id.txt_showProjectName);
				txt_desc = (TextView) view.findViewById(R.id.txt_showProjectDesc);
				txt_lead = (TextView) view.findViewById(R.id.txt_showProjectLeader);
				
				
				
				
				
				
				image = (ImageView) view.findViewById(R.id.img_projectPic);
				
			}
			
			
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			View row = convertView;
			myViewHolder holder = null;
			
			if (row == null){
				inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.ws_projectsrow, parent, false);
				holder = new myViewHolder(row);
				row.setTag(holder);
			}else{
				holder = (myViewHolder) row.getTag();
			}
			
			
				holder.txt_id.setText(project_idX.get(position));
				holder.txt_name.setText(project_nameX.get(position));
				
				holder.txt_lead.setText(project_leaderX.get(position));
				
				if(project_descX.get(position).toString().equals("")){
					holder.txt_desc.setVisibility(View.GONE);
				}else{
					holder.txt_desc.setVisibility(View.VISIBLE);
					holder.txt_desc.setText(project_descX.get(position));
				}
				
				//holder.image.setImageBitmap(getRoundedShape(avatarsX.get(position)));
				
			
			
			return row;
		}
	}
	
	
}
