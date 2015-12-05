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


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class GoalOpen extends Activity {
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	
	SharedPreferences prefs;
	Integer user_id, workspace_id, ws_user_level_id, project_id, p_user_level_id;
	ListView lv_goalsopen;
	
	LayoutInflater inflater;
	//Bitmap bitmap;
	JSONArray open_goals;
	Boolean toStart;
	int toStartId;
	
	
	public List<String> goal_id = new ArrayList<String>();
	public List<String> goal_name = new ArrayList<String>();
	public List<String> goal_creator = new ArrayList<String>();
	public List<String> goal_creator_id = new ArrayList<String>();
	public List<String> goal_date = new ArrayList<String>();
	public List<String> goal_tasks = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		toStartId = 0;
		toStart = false;
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.goal);
		
		prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
		lv_goalsopen = (ListView) findViewById(R.id.listViewGoals);
		user_id = prefs.getInt("user_id", 0);
		project_id = prefs.getInt("project_id", 0);
		
		lv_goalsopen.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		lv_goalsopen.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				TextView goal_id = (TextView) view.findViewById(R.id.txt_goalrowId);
				TextView goal_name = (TextView) view.findViewById(R.id.txt_goalrowName);
				TextView goal_creator = (TextView) view.findViewById(R.id.txt_goalrowCreator);
				TextView goal_creator_id = (TextView) view.findViewById(R.id.txt_goalrowCreatorId);
				TextView goal_date = (TextView) view.findViewById(R.id.txt_goalrowDate);
				
				Intent i = new Intent(GoalOpen.this, GoalView.class);
				i.putExtra("goal_id", Integer.parseInt(goal_id.getText().toString()));
				i.putExtra("goal_name", goal_name.getText().toString());
				i.putExtra("goal_creator", goal_creator.getText().toString());
				i.putExtra("goal_creator_id", Integer.parseInt(goal_creator_id.getText().toString()));
				i.putExtra("goal_date", goal_date.getText().toString());
				
				startActivity(i);
			}
		});
		
		
		
		loadOpenGoals();
		
		
	}
	
	public void loadOpenGoals() {
		// TODO Auto-generated method stub
		new NetCheck().execute();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		loadOpenGoals();
		super.onResume();
	}

	public class NetCheck extends AsyncTask<Void, Void, Boolean>{
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			getParent().setProgressBarIndeterminateVisibility(true);
			
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
				getParent().setProgressBarIndeterminateVisibility(false);
                
				
				
                if (toStart==true){
                	toStart = false;
                	new startGoal().execute();
                }else{
                	new getOpenGoals().execute();
                }

            }
            else{
            	getParent().setProgressBarIndeterminateVisibility(false);
            	
            	Toast.makeText(getBaseContext(), "Unable to connect to the network", Toast.LENGTH_SHORT).show();
            	
                
                
            }
		}
		
	}
	
	public class getOpenGoals extends AsyncTask<Void, Void, JSONObject>{
		
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			getParent().setProgressBarIndeterminateVisibility(true);
			
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			//Log.e("aaa",""+ws_id);
			userFunctions uf = new userFunctions();
			JSONObject json = uf.getOpenGoals(project_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					getParent().setProgressBarIndeterminateVisibility(false);
					
					open_goals = json.getJSONArray("open_goals");
					GoalOpen.this.goal_id.clear();
					GoalOpen.this.goal_name.clear();
					GoalOpen.this.goal_creator.clear();
					GoalOpen.this.goal_creator_id.clear();
					GoalOpen.this.goal_date.clear();
					GoalOpen.this.goal_tasks.clear();
					
					
					
					for(int i=0; i< open_goals.length(); i++){
						JSONObject z = open_goals.getJSONObject(i);
						
						String goal_id = ""+z.getInt("goal_id");
						String goal_name = z.getString("goal_name");
						String goal_creator = z.getString("goal_creator");
						String goal_creator_id = z.getString("goal_creator_id");
						String goal_date = z.getString("date_created");
						String goal_tasks = z.getString("tasks");
						
						
						
						
						GoalOpen.this.goal_id.add(goal_id);
						GoalOpen.this.goal_name.add(goal_name);
						GoalOpen.this.goal_creator.add(goal_creator);
						GoalOpen.this.goal_creator_id.add(goal_creator_id);
						GoalOpen.this.goal_date.add(goal_date);
						GoalOpen.this.goal_tasks.add(goal_tasks);
						
						
						
						
						
						
					}
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							
							//wsShowMembers.setVisibility(LinearLayout.VISIBLE);
							//loader.setVisibility(LinearLayout.GONE);
							openGoalsAdapter adapter = new openGoalsAdapter(getApplicationContext(), goal_id, goal_name, goal_creator, goal_creator_id,goal_date, goal_tasks);
							lv_goalsopen.setAdapter(adapter);
							lv_goalsopen.setEmptyView(findViewById(R.id.no_goals));
							adapter.notifyDataSetChanged();
							
						}
					});
			
				}else{
					getParent().setProgressBarIndeterminateVisibility(false);
					
					GoalOpen.this.goal_id.clear();
					GoalOpen.this.goal_name.clear();
					GoalOpen.this.goal_creator.clear();
					GoalOpen.this.goal_creator_id.clear();
					GoalOpen.this.goal_date.clear();
					GoalOpen.this.goal_tasks.clear();
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							
							openGoalsAdapter adapter = new openGoalsAdapter(getApplicationContext(), goal_id, goal_name, goal_creator, goal_creator_id,goal_date, goal_tasks);
							lv_goalsopen.setAdapter(adapter);
							lv_goalsopen.setEmptyView(findViewById(R.id.no_goals));
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
	
	public class startGoal extends AsyncTask<Void, Void, JSONObject>{
		
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			getParent().setProgressBarIndeterminateVisibility(true);
			
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			//Log.e("aaa",""+ws_id);
			userFunctions uf = new userFunctions();
			JSONObject json = uf.startGoal(user_id, toStartId);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					getParent().setProgressBarIndeterminateVisibility(false);
					
					Toast.makeText(getBaseContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
					loadOpenGoals();
			
				}else{
					getParent().setProgressBarIndeterminateVisibility(false);
					
					
					
					//Toast.makeText(getBaseContext(), "Couldnt retrieve information", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	
	class openGoalsAdapter extends ArrayAdapter<String>{
		

		Context context;
		List<String> goal_idX;
		List<String> goal_nameX;
		List<String> goal_creatorX;
		List<String> goal_creator_idX;
		List<String> goal_dateX;
		List<String> goal_tasksX;
		
		
		openGoalsAdapter(Context c, List<String> goal_id, List<String> goal_name, List<String> goal_creator, List<String> goal_creator_id,List<String> goal_date, List<String> goal_tasks){
			super(c,R.layout.goal_row,R.id.txt_goalrowId, goal_id);
			this.context = c;
			this.goal_idX = goal_id;
			this.goal_nameX = goal_name;
			this.goal_creatorX = goal_creator;
			this.goal_creator_idX = goal_creator_id;
			this.goal_dateX = goal_date;
			this.goal_tasksX = goal_tasks;
			
		}
		
		class myViewHolder{
			TextView txt_id, txt_goal_name, txt_creator,txt_creator_id, txt_ago, txt_tasks;
			Button btn_start;
			ProgressBar prog;
			
			
			
			public myViewHolder(View view) {
				txt_id = (TextView) view.findViewById(R.id.txt_goalrowId);
				txt_goal_name = (TextView) view.findViewById(R.id.txt_goalrowName);
				txt_creator = (TextView) view.findViewById(R.id.txt_goalrowCreator);
				txt_creator_id = (TextView) view.findViewById(R.id.txt_goalrowCreatorId);
				txt_ago = (TextView) view.findViewById(R.id.txt_goalrowDate);
				txt_tasks = (TextView) view.findViewById(R.id.txt_goalrowTasks);
				btn_start = (Button) view.findViewById(R.id.btn_startGoal);
				prog = (ProgressBar) view.findViewById(R.id.progressbar_goalrow);
				
				
				
			}
			
			
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			View row = convertView;
			myViewHolder holder = null;
			
			if (row == null){
				inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.goal_row, parent, false);
				holder = new myViewHolder(row);
				row.setTag(holder);
			}else{
				holder = (myViewHolder) row.getTag();
			}
				holder.prog.setVisibility(View.GONE);
			
				holder.txt_id.setText(goal_idX.get(position));
				holder.txt_goal_name.setText(goal_nameX.get(position));
				holder.txt_creator.setText(goal_creatorX.get(position));
				holder.txt_creator_id.setText(goal_creator_idX.get(position));
				holder.txt_ago.setText(goal_dateX.get(position));
				holder.txt_tasks.setText(Html.fromHtml(goal_tasksX.get(position)));
				
				holder.btn_start.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						AlertDialog.Builder adb = new AlertDialog.Builder(GoalOpen.this);
						adb.setTitle("Confirmation");
						adb.setMessage("Do you want the team to start working on this goal?");
						adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								toStart = true;
								toStartId = Integer.parseInt(goal_idX.get(position));
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
				

				
				
				
			
			
			return row;
		}
	}
}
