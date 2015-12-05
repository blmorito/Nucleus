package com.example.nucleus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;










import com.example.nucleus.DiscussionView.NetCheck;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class GoalView extends Activity {
	
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	SharedPreferences prefs;
	int goal_creator_id, goal_id, user_id, project_id, p_user_level_id;
	LayoutInflater inflater;
	JSONArray tasks;
	public List<String> task_id = new ArrayList<String>();
	public List<String> task_name = new ArrayList<String>();
	public List<String> task_status = new ArrayList<String>();
	public List<String> task_date_due = new ArrayList<String>();
	public List<String> task_is_late = new ArrayList<String>();
	public List<String> task_assignee = new ArrayList<String>();
	public List<String> task_assigneeId = new ArrayList<String>();
	
	//forcomments
	public List<String> c_id = new ArrayList<String>();
	public List<String> c_author = new ArrayList<String>();
	public List<String> c_author_id = new ArrayList<String>();
	public List<String> c_content = new ArrayList<String>();
	public List<String> c_date = new ArrayList<String>();
	public List<Bitmap> c_avatar = new ArrayList<Bitmap>();
	Bitmap bitmap, bitmap2;
	JSONArray comments;
	
	EditText edt_comment;
	Button btn_comment;
	
	TextView txt_goalName, txt_goalCreator, txt_goalDate, txt_goalStatus, txt_goalDesc;
	ListView lv_tasks, lv_comments;
	
	Boolean toChange;
	Boolean toComment;
	Boolean toDeleteComment;
	Boolean toUpdateComment;
	String toUpdateCommentContent;
	String toCommentContent;
	int taskToChange;
	int toDeleteCommentId;
	int toUpdateCommentId;
	
	Boolean toDelete;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		setContentView(R.layout.goal_view);
		
		prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
		//booolean
		toChange = false;
		toComment = false;
		toDelete = false;
		toDeleteComment = false;
		toUpdateComment = false;
		//
		user_id = prefs.getInt("user_id", 0);
		project_id = prefs.getInt("project_id", 0);
		p_user_level_id = prefs.getInt("p_user_level_id", 0);
		
		lv_tasks = (ListView) findViewById(R.id.listViewCheckTasks);
		lv_comments = (ListView) findViewById(R.id.listViewCheckTasksComments);
		txt_goalName = (TextView) findViewById(R.id.txt_goalviewName);
		txt_goalCreator = (TextView) findViewById(R.id.txt_goalviewCreator);
		txt_goalDate = (TextView) findViewById(R.id.txt_goalviewDateCreated);
		txt_goalStatus = (TextView) findViewById(R.id.txt_goalviewStatus);
		txt_goalDesc = (TextView) findViewById(R.id.txt_goalviewDesc);
		
		goal_id = getIntent().getIntExtra("goal_id", 0);
		goal_creator_id = getIntent().getIntExtra("goal_creator_id", 0);
		
		txt_goalName.setText(getIntent().getStringExtra("goal_name"));
		txt_goalCreator.setText(getIntent().getStringExtra("goal_creator")+" •");
		txt_goalDate.setText(getIntent().getStringExtra("goal_date")+" •");
		setTitle(getIntent().getStringExtra("goal_name"));
		//
		
		edt_comment = (EditText) findViewById(R.id.edt_goalComment);
		btn_comment = (Button) findViewById(R.id.btn_goalComment);
		edt_comment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edt_comment.setError(null);
			}
		});
		btn_comment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(edt_comment.getText().toString().trim().equals("")){
					edt_comment.setError("You cant post a blank comment!");
				}else{
					toComment = true;
					toCommentContent = edt_comment.getText().toString().trim();
					
					edt_comment.setText("");
					 InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			         imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
					new NetCheck().execute();
				}
			}
		});
		
		lv_tasks.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				TextView task_id = (TextView) view.findViewById(R.id.txt_checktaskId);
				TextView task_name = (TextView) view.findViewById(R.id.txt_checktaskName);
				TextView task_date = (TextView) view.findViewById(R.id.txt_checktaskDue);
				TextView task_status = (TextView) view.findViewById(R.id.txt_checktaskStatus);
				TextView task_isLate = (TextView) view.findViewById(R.id.txt_checktaskIsLate);
				TextView task_assignee = (TextView) view.findViewById(R.id.txt_checktaskAssignee);
				
				Intent i = new Intent(GoalView.this, TaskView.class);
				i.putExtra("task_id", Integer.parseInt(task_id.getText().toString()));
				i.putExtra("task_name", task_name.getText().toString());
				i.putExtra("task_date", task_date.getText().toString());
				i.putExtra("task_status", task_status.getText().toString());
				i.putExtra("task_isLate", task_isLate.getText().toString());
				i.putExtra("task_assignee", task_assignee.getText().toString());
				i.putExtra("task_count", GoalView.this.task_id.size());
				
				startActivity(i);
				
				
				
			}
		});
		
		lv_comments.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				TextView commentor_id = (TextView) view.findViewById(R.id.txt_commentRowAuthorId);
				final TextView comment_id = (TextView) view.findViewById(R.id.txt_commentRowId);
				final TextView comment_content = (TextView) view.findViewById(R.id.txt_commonRowContent);
				
				if(Integer.parseInt(commentor_id.getText().toString())== user_id || goal_creator_id==user_id || p_user_level_id == 1){
					AlertDialog.Builder adb1 = new AlertDialog.Builder(GoalView.this);
					final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(GoalView.this,
		                    android.R.layout.select_dialog_item);
		            arrayAdapter.add("Edit comment");
		            arrayAdapter.add("Delete this comment");
		            adb1.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String choice = arrayAdapter.getItem(which);
							if(choice.equals("Edit comment")){
								AlertDialog.Builder builderInner = new AlertDialog.Builder(
	                        			GoalView.this);
	                            //builderInner.setMessage("Edit your comment here");
	                            builderInner.setTitle("Update comment");
	                            final EditText edittext= new EditText(GoalView.this);
	                            edittext.setOnClickListener(new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										edittext.setError(null);
									}
								});
	                            builderInner.setView(edittext);
	                            String timeStamp = new SimpleDateFormat("EEE, d MMM yyyy 'at' h:mm a").format(Calendar.getInstance().getTime());
	                            edittext.setText(comment_content.getText().toString());
	                            builderInner.setPositiveButton("Update",
	                                    new DialogInterface.OnClickListener() {

	                                        @Override
	                                        public void onClick( DialogInterface dialog,int which) {
	                                        	if (edittext.getText().toString().trim().equals("")){
	                                        		edittext.setError("You cannot post a blank comment");
	                                        	}else{
	                                        		toUpdateComment = true;
		                                        	toUpdateCommentId = Integer.parseInt(comment_id.getText().toString());
		                                        	toUpdateCommentContent = edittext.getText().toString();
		                                        	new NetCheck().execute();
	                                        	}
	                                        }
	                                    });
	                            builderInner.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										
									}
								});
	                            builderInner.show();
							}else{
								AlertDialog.Builder builderInner = new AlertDialog.Builder(
	                        			GoalView.this);
	                            builderInner.setMessage("Are you sure you want to delete this comment?");
	                            builderInner.setTitle("Confirmation");
	                            builderInner.setPositiveButton("Yes",
	                                    new DialogInterface.OnClickListener() {

	                                        @Override
	                                        public void onClick( DialogInterface dialog,int which) {
	                                        	toDeleteCommentId = Integer.parseInt(comment_id.getText().toString());
	                                        	toDeleteComment = true;
	                                        	new NetCheck().execute();
	                                            dialog.dismiss();
	                                        }
	                                    });
	                            builderInner.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										
									}
								});
	                            builderInner.show();
							}
						}
					});
		            
		            adb1.show();
					
				}
				
				return false;
			}
		});
		
		
		
		
		new NetCheck().execute();
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.goal_view_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_goalview_addtask:
			Intent i = new Intent(GoalView.this, TaskNew.class);
			i.putExtra("goal_id", goal_id);
			i.putExtra("goal_name", txt_goalName.getText().toString());
			startActivity(i);
			return true;
		case R.id.action_goalview_refresh:
			new NetCheck().execute();
			return true;
		case R.id.action_goalview_editgoal:
			AlertDialog.Builder adb = new AlertDialog.Builder(GoalView.this);
			adb.setTitle("Confirmation");
			adb.setMessage("Editing the information on this goal might confuse other members. Do you wish to proceed?");
			adb.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Intent i = new Intent(GoalView.this, GoalEdit.class);
					i.putExtra("goal_id", goal_id);
					i.putExtra("goal_name", txt_goalName.getText().toString());
					i.putExtra("goal_desc", txt_goalDesc.getText().toString());
					startActivity(i);
				}
			});
			adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
			adb.show();
			return true;
		case R.id.action_goalview_deletegoal:
			AlertDialog.Builder adb2 = new AlertDialog.Builder(GoalView.this);
			adb2.setTitle("Confirmation");
			adb2.setMessage("Are you sure you want to delete this goal?");
			adb2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					toDelete = true;
					new NetCheck().execute();
				}
			});
			adb2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
			adb2.show();
			return true;
			
		case R.id.action_goalview_backtodashboard:
			
			Intent x = new Intent(GoalView.this, Dashboard.class);
			x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(x);
				

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		new NetCheck().execute();
		super.onResume();
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
                
				if(toChange==true){
					toChange=false;
					new updateTask().execute();
					
					Log.e("after netcheck","to change");
					
				}else if (toComment==true) {
					toComment = false;
					new postComment().execute();
					Log.e("after netcheck","to comment");
					
					
				}else if (toDelete==true) {
					toDelete = false;
					new DeleteGoal().execute();
					Log.e("after netcheck","to delete");
				}else if (toDeleteComment==true) {
					toDeleteComment = false;
					new DeleteComment().execute();
					Log.e("after netcheck","to delete commmnt");
					
				}else if (toUpdateComment==true) {
					toUpdateComment = false;
					new UpdateComment().execute();
					Log.e("after netcheck","to to updatecomm");
				}else{
					new getGoalInfoTasks().execute();
					Log.e("after netcheck","defaulttt");
				}
				
               
            }
            else{
            	setProgressBarIndeterminateVisibility(false);
            	
            	Toast.makeText(getBaseContext(), "Unable to connect to the network", Toast.LENGTH_SHORT).show();
            	
                
                
            }
		}
		
	}
	
	public class getGoalInfoTasks extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
			JSONObject json = uf.getGoalInfoTasks(goal_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					
					Log.e("ni sud ka", "oh");
					setProgressBarIndeterminateVisibility(false);
					
					if(json.getString("goal_name").equals("null")){
						
						finish();
					}
					
					txt_goalName.setText(json.getString("goal_name"));
					txt_goalDesc.setText(json.getString("goal_desc"));
					
					Log.e("ni sud ka", "ohlge");
					txt_goalDate.setText(json.getString("date_created"));
					Log.e("ni sud ka", "ohlge2");
					txt_goalStatus.setText(json.getString("status"));
					Log.e("ni sud ka", "ohlge3");
					Log.e("statussss", json.getString("status"));
					Log.e("ni sud ka", "ohlge4");
					tasks = json.getJSONArray("tasks");
					Log.e("ni sud ka", "ohlge5");
					
					Log.e("something",tasks.toString());
					Log.e("task length",""+tasks.length());
					GoalView.this.task_id.clear();
					GoalView.this.task_name.clear();
					GoalView.this.task_date_due.clear();
					GoalView.this.task_status.clear();
					GoalView.this.task_is_late.clear();
					GoalView.this.task_assignee.clear();
					GoalView.this.task_assigneeId.clear();
					
					for(int i=0; i<tasks.length();i++){
						Log.e("in loop","yes");
						JSONObject z = tasks.getJSONObject(i);
						String t_id = ""+z.getInt("task_id");
						String t_name = z.getString("task_name");
						String t_date_due = z.getString("date_due");
						String t_status = z.getString("task_status");
						String t_islate = z.getString("isLate");
						String t_assigneeId = ""+z.getInt("task_assignee_id");
						String t_assignee = z.getString("task_assignee");
						
						GoalView.this.task_id.add(t_id);
						GoalView.this.task_name.add(t_name);
						GoalView.this.task_date_due.add(t_date_due);
						GoalView.this.task_status.add(t_status);
						GoalView.this.task_is_late.add(t_islate);
						GoalView.this.task_assignee.add(t_assignee);
						GoalView.this.task_assigneeId.add(t_assigneeId);
					}
					
						runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							
								//wsShowMembers.setVisibility(LinearLayout.VISIBLE);
								//loader.setVisibility(LinearLayout.GONE);
								tasksAdapter adapter = new tasksAdapter(getApplicationContext(), task_id, task_name, task_date_due, task_status,task_is_late,task_assignee,task_assigneeId);
								lv_tasks.setAdapter(adapter);
								Log.e("check agin","went to update task adaptaer");
								
								//lv_tasks.setEmptyView(findViewById(R.id.no_goals));
								adapter.notifyDataSetChanged();
								ListUtils.setDynamicHeight(lv_tasks);
								
								
							}
						});
						
						new viewGoalComments().execute();
				
				}else{
					setProgressBarIndeterminateVisibility(false);
					GoalView.this.task_id.clear();
					GoalView.this.task_name.clear();
					GoalView.this.task_date_due.clear();
					GoalView.this.task_status.clear();
					GoalView.this.task_is_late.clear();
					GoalView.this.task_assignee.clear();
					GoalView.this.task_assigneeId.clear();
					tasksAdapter adapter = new tasksAdapter(getApplicationContext(), task_id, task_name, task_date_due, task_status,task_is_late,task_assignee,task_assigneeId);
					lv_tasks.setAdapter(adapter);
					
					//lv_tasks.setEmptyView(findViewById(R.id.no_goals));
					adapter.notifyDataSetChanged();
					ListUtils.setDynamicHeight(lv_tasks);
					
					
					//Toast.makeText(getBaseContext(), "Couldnt retrieve information", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				//Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	
	public class DeleteComment extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
			JSONObject json = uf.deleteComment(toDeleteCommentId);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					Toast.makeText(getBaseContext(), "Successfully deleted a comment", Toast.LENGTH_SHORT).show();
					new NetCheck().execute();
					
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					
					Toast.makeText(getBaseContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	
	public class UpdateComment extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
			JSONObject json = uf.updateCommentDiscussion(toUpdateCommentId, toUpdateCommentContent);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					Toast.makeText(getBaseContext(), "Successfully updated a comment", Toast.LENGTH_SHORT).show();
					new NetCheck().execute();
					
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					
					Toast.makeText(getBaseContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	
	public class postComment extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
			JSONObject json = uf.postCommentGoal(user_id, goal_id, toCommentContent);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					new NetCheck().execute();
					Toast.makeText(getBaseContext(), "Comment posted", Toast.LENGTH_SHORT).show();
				
				}else{
					setProgressBarIndeterminateVisibility(false);
					Toast.makeText(getBaseContext(), "Something went wrong. Pls try again.", Toast.LENGTH_SHORT).show();
					
					
					//Toast.makeText(getBaseContext(), "Couldnt retrieve information", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				//Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	public class DeleteGoal extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
			JSONObject json = uf.deleteGoal(user_id, goal_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					Toast.makeText(getBaseContext(), "Successfully deleted a goal", Toast.LENGTH_SHORT).show();;
					finish();
				
				}else{
					setProgressBarIndeterminateVisibility(false);
					Log.e("fromDELETE", json.toString());
					Toast.makeText(getBaseContext(), "Something went wrong. Pls try again.", Toast.LENGTH_SHORT).show();
					
					
					//Toast.makeText(getBaseContext(), "Couldnt retrieve information", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	
	public class updateTask extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
			JSONObject json = uf.updateTask(user_id, goal_id, taskToChange);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					Toast.makeText(getBaseContext(), "Updated the task", Toast.LENGTH_SHORT).show();
					new NetCheck().execute();
				
				}else{
					setProgressBarIndeterminateVisibility(false);
					Toast.makeText(getBaseContext(), "Unable to update the task. Please try again.", Toast.LENGTH_SHORT).show();
					new NetCheck().execute();
					
					
					//Toast.makeText(getBaseContext(), "Couldnt retrieve information", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	
	class tasksAdapter extends ArrayAdapter<String>{
		

		Context context;
		List<String> task_idX;
		List<String> task_nameX;
		List<String> task_date_dueX;
		List<String> task_statusX;
		List<String> task_is_lateX;
		List<String> task_assigneeIdX;
		List<String> task_assigneeX;
		
		
		
		tasksAdapter(Context c, List<String> task_id, List<String> task_name, List<String> task_date_due, List<String> task_status,List<String> task_is_late, List<String> task_assignee, List<String> task_assigneeId){
			super(c,R.layout.goal_view_taskrow,R.id.txt_checktaskId, task_id);
			this.context = c;
			this.task_idX = task_id;
			this.task_nameX = task_name;
			this.task_date_dueX = task_date_due;
			this.task_statusX = task_status;
			this.task_is_lateX = task_is_late;
			this.task_assigneeX = task_assignee;
			this.task_assigneeIdX = task_assigneeId;
			
			
		}
		
		class myViewHolder{
			
			TextView txt_t_id, txt_t_name, txt_t_due, txt_t_status, txt_t_islate,txt_t_assignee, txt_t_assigneeId;
			CheckBox cb_task;
			
			
			
			
			public myViewHolder(View view) {
				txt_t_id = (TextView) view.findViewById(R.id.txt_checktaskId);
				txt_t_name = (TextView) view.findViewById(R.id.txt_checktaskName);
				txt_t_due = (TextView) view.findViewById(R.id.txt_checktaskDue);
				txt_t_status = (TextView) view.findViewById(R.id.txt_checktaskStatus);
				txt_t_islate = (TextView) view.findViewById(R.id.txt_checktaskIsLate);
				txt_t_assignee = (TextView) view.findViewById(R.id.txt_checktaskAssignee);
				txt_t_assigneeId = (TextView) view.findViewById(R.id.txt_checktaskAssigneeId);
				
				cb_task = (CheckBox) view.findViewById(R.id.cb_taskrow);
				
				
				
				
			}
			
			
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return this.task_idX.size();
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			View row = convertView;
			myViewHolder holder = null;
			
			if (row == null){
				inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.goal_view_taskrow, parent, false);
				holder = new myViewHolder(row);
				row.setTag(holder);
			}else{
				holder = (myViewHolder) row.getTag();
			}
				//holder.prog.setVisibility(View.GONE);
			
				holder.txt_t_id.setText(task_idX.get(position));
				holder.txt_t_name.setText(task_nameX.get(position));
				holder.txt_t_due.setText(task_date_dueX.get(position));
				holder.txt_t_status.setText(task_statusX.get(position));
				holder.txt_t_islate.setText(task_is_lateX.get(position));
				
				holder.txt_t_assigneeId.setText(task_assigneeIdX.get(position));
				
				if(task_statusX.get(position).equals("Open")){
					holder.cb_task.setChecked(false);
				}else if (task_statusX.get(position).equals("In progress")) {
					holder.cb_task.setChecked(false);
				}else{
					holder.cb_task.setChecked(true);
				}
				
				if(task_is_lateX.get(position).equals("Not")){
					holder.txt_t_due.setTextColor(Color.DKGRAY);
				}else{
					holder.txt_t_due.setTextColor(Color.RED);
				}
				
				if(Integer.parseInt(task_assigneeIdX.get(position))==user_id){
					holder.txt_t_assignee.setText("You");
				}else{
					holder.txt_t_assignee.setText(task_assigneeX.get(position));
				}
				
				holder.cb_task.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
						Log.e("see", task_statusX.get(position)+" "+txt_goalStatus.getText().toString());
						if (task_statusX.get(position).equals("Done")&& txt_goalStatus.getText().toString().equals("Status: Done")){
							Log.e("went here", "went 1 here");
							AlertDialog.Builder adb = new AlertDialog.Builder(GoalView.this);
							adb.setTitle("Reopen Task");
							adb.setMessage("You are trying to reopen a task from a completed goal. Doing so will change the goal's status to In Progress. Continue?");
							adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									toChange = true;
									taskToChange = Integer.parseInt(task_idX.get(position));
									new NetCheck().execute();
								}
							});
							adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									new NetCheck().execute();
								}
							});
							adb.show();
							
							
						}else if (txt_goalStatus.getText().toString().equals("Status: Open")) {
							Log.e("went here", "went 2 here");
							AlertDialog.Builder adb = new AlertDialog.Builder(GoalView.this);
							adb.setTitle("Confirmation");
							adb.setMessage("You are trying to complete a task from an open goal. Doing so will start the goal and change its status. Continue?");
							adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									toChange = true;
									taskToChange = Integer.parseInt(task_idX.get(position));
									new NetCheck().execute();
								}
							});
							adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									new NetCheck().execute();
								}
							});
							adb.show();
						}else{
							Log.e("went here", "went 3 here");
							toChange = true;
							taskToChange = Integer.parseInt(task_idX.get(position));
							new NetCheck().execute();
						}
					}
				});
				
			
			
			return row;
		}
	}
	
	public class viewGoalComments extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
			JSONObject json = uf.viewGoalComments(goal_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
			    	
			    	final Boolean hasComments;
			    	//get comments
			    	GoalView.this.c_id.clear();
			    	GoalView.this.c_author.clear();
			    	GoalView.this.c_author_id.clear();
			    	GoalView.this.c_content.clear();
			    	GoalView.this.c_date.clear();
			    	GoalView.this.c_avatar.clear();
			    	Log.e("hasCOmment",""+json.getBoolean("hasComments"));
			    	if(json.getBoolean("hasComments")==true){
			    		hasComments = true;
			    		comments = json.getJSONArray("comments");
			    		for(int i=0; i< comments.length(); i++){
							JSONObject z = comments.getJSONObject(i);
							
							String c_id = ""+z.getInt("comment_id");
							String c_content = z.getString("comment_content");
							String c_author = z.getString("comment_author");
							String c_author_id = z.getString("comment_author_id");
							String c_date = z.getString("comment_ago");
							
							String avatar_path = z.getString("comment_avatar");
							
							GoalView.this.c_id.add(c_id);
							GoalView.this.c_author.add(c_author);
							GoalView.this.c_author_id.add(c_author_id);
							GoalView.this.c_content.add(c_content);
							GoalView.this.c_date.add(c_date);
							
							
							byte[] blobimg1 = Base64.decode(avatar_path.getBytes(), 1);
					    	bitmap2 = BitmapFactory.decodeByteArray(blobimg1, 0, blobimg1.length);
							Log.e("bitmap2", ""+bitmap2);
							GoalView.this.c_avatar.add(bitmap2);
					    	
					    	
							
						}
			    	}else{
			    		
			    		hasComments = false;
			    		
			    		
			    	}
			    	
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							
							
						    
						    if(hasComments==true){
						    	CommentsAdapter adapter = new CommentsAdapter(getApplicationContext(), c_id, c_author,c_author_id, c_content, c_date, c_avatar);
						    	lv_comments.setAdapter(adapter);
						    	
						    	lv_comments.setEmptyView(findViewById(R.id.no_goal_comments));
						    	adapter.notifyDataSetChanged();
						    	ListUtils.setDynamicHeight(lv_comments);
						    	
						    	
						    }else{
						    	CommentsAdapter adapter = new CommentsAdapter(getApplicationContext(), c_id, c_author,c_author_id, c_content, c_date, c_avatar);
						    	lv_comments.setAdapter(adapter);
						    	lv_comments.setEmptyView(findViewById(R.id.no_goal_comments));
						    	
						    	adapter.notifyDataSetChanged();
						    	ListUtils.setDynamicHeight(lv_comments);
						    }
							
						}
					});
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					
					
					//Toast.makeText(getBaseContext(), "Couldnt retrieve information", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	
	class CommentsAdapter extends ArrayAdapter<String>{
		Context context;
		List<String> c_idX;
		List<String> c_authorX;
		List<String> c_author_idX;
		List<String> c_contentX;
		List<String> c_dateX;
		
		List<Bitmap> c_avatarX;
		
		CommentsAdapter(Context c, List<String> c_id, List<String> c_author,List<String> c_author_id, List<String> c_content, List<String> c_date,List<Bitmap> c_avatar){
			super(c,R.layout.comment_row,R.id.txt_commentRowId, c_id);
			this.context = c;
			this.c_idX = c_id;
			this.c_authorX = c_author;
			this.c_author_idX = c_author_id;
			this.c_contentX = c_content;
			this.c_dateX = c_date;
			this.c_avatarX = c_avatar;
			
		}
		
		class myViewHolder{
			TextView txt_id, txt_content, txt_c_author,txt_c_author_id, txt_ago;
			ImageView img_pic;
			
			
			
			public myViewHolder(View view) {
				txt_id = (TextView) view.findViewById(R.id.txt_commentRowId);
				txt_content = (TextView) view.findViewById(R.id.txt_commonRowContent);
				txt_c_author = (TextView) view.findViewById(R.id.txt_commentRowAuthor);
				txt_c_author_id = (TextView) view.findViewById(R.id.txt_commentRowAuthorId);
				txt_ago = (TextView) view.findViewById(R.id.txt_commentRowDate);
				img_pic = (ImageView) view.findViewById(R.id.img_commentRowPic);
				
				
				
				
				
				
			}
			
			
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return this.c_idX.size();
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			View row = convertView;
			myViewHolder holder = null;
			
			if (row == null){
				inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.comment_row, parent, false);
				holder = new myViewHolder(row);
				row.setTag(holder);
			}else{
				holder = (myViewHolder) row.getTag();
			}
			
			
				holder.txt_id.setText(c_idX.get(position));
				holder.txt_ago.setText(c_dateX.get(position));
				holder.txt_c_author.setText(c_authorX.get(position));
				holder.txt_c_author_id.setText(c_author_idX.get(position));
				holder.txt_content.setText(c_contentX.get(position));
				holder.img_pic.setImageBitmap(getRoundedShape(c_avatarX.get(position)));
				//holder.image.setImageBitmap(getRoundedShape(avatarsX.get(position)));
				
				holder.txt_c_author.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent i = new Intent(GoalView.this, DashboardMe.class);
						i.putExtra("user_id", c_author_id.get(position));
						i.putExtra("profile_name",c_author.get(position));
						startActivity(i);
					}
				});
				
				holder.img_pic.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent i = new Intent(GoalView.this, DashboardMe.class);
						i.putExtra("user_id", c_author_id.get(position));
						i.putExtra("profile_name",c_author.get(position));
						startActivity(i);
					}
				});
				
				
				
			
			
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
	
	public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = MeasureSpec.makeMeasureSpec(mListView.getWidth(), MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }
	
	
	

}
