package com.example.nucleus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;





import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.nucleus.GoalView.CommentsAdapter;
import com.example.nucleus.GoalView.DeleteComment;
import com.example.nucleus.GoalView.ListUtils;
import com.example.nucleus.GoalView.NetCheck;
import com.example.nucleus.GoalView.UpdateComment;
import com.example.nucleus.GoalView.CommentsAdapter.myViewHolder;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.DatePickerDialog.OnDateSetListener;
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
import android.graphics.YuvImage;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TaskView extends Activity {
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	SharedPreferences prefs;
	int goal_id,task_id, user_id, project_id, p_user_level_id, task_count;
	
	int toDeleteId;
	Boolean toDelete;
	String toCommentContent;
	CheckBox cb_task;
	TextView txt_taskAssignment, txt_taskDate, txt_taskCreator, txt_top, txt_taskStatus;
	Button btn_assign, btn_deadline, btn_save, btn_startTask;
	EditText edt_taskName;
	
	Boolean toDeleteComment;
	Boolean toUpdateComment;
	String toUpdateCommentContent;
	int toDeleteCommentId;
	int toUpdateCommentId;
	
	
	//forcomments
	public List<String> c_id = new ArrayList<String>();
	public List<String> c_author = new ArrayList<String>();
	public List<String> c_author_id = new ArrayList<String>();
	public List<String> c_content = new ArrayList<String>();
	public List<String> c_date = new ArrayList<String>();
	public List<Bitmap> c_avatar = new ArrayList<Bitmap>();
	Bitmap bitmap;
	JSONArray comments;
	
	//newly added views
	ListView listView;
	Button btn_addComment;
	EditText edt_addComment;
	//etc
	Boolean toPostComment;
	
	LayoutInflater inflater;
	Integer assignId;
	String due_date;
	
	//bools
	Boolean toSave;
	Boolean toUpdate;
	Boolean toStart;
	
	JSONArray members;
	public List<String> membersId = new ArrayList<String>();
	public List<String> membersName = new ArrayList<String>();
	
	//getextras
	String task_name, task_date, task_status, task_isLate, task_assignee;
	
	String project_deadline;
	DatePickerDialog datepicker_dueDate;
	SimpleDateFormat dateFormatter,dateFinalFormatter;
	Calendar cal;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		setContentView(R.layout.task_view);
		
		
		toDeleteComment = false;
		toUpdateComment = false;
		
		
		toSave=false;
		toUpdate = false;
		toDelete = false;
		toStart = false;
		toPostComment = false;
		
		prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
		btn_assign = (Button) findViewById(R.id.btn_taskviewAssign);
		btn_deadline = (Button) findViewById(R.id.btn_taskviewDue);
		btn_startTask = (Button) findViewById(R.id.btn_startTask);
		btn_save = (Button) findViewById(R.id.btn_taskviewSave);
		txt_taskAssignment = (TextView) findViewById(R.id.txt_taskviewAssign);
		txt_taskCreator = (TextView) findViewById(R.id.txt_taskCreator);
		txt_taskDate = (TextView) findViewById(R.id.txt_taskviewDue);
		txt_top = (TextView) findViewById(R.id.textView5tops);
		txt_taskStatus = (TextView) findViewById(R.id.txt_taskStatus);
		cb_task = (CheckBox) findViewById(R.id.cb_taskkk);
		edt_taskName = (EditText) findViewById(R.id.edt_taskviewName);
		user_id = prefs.getInt("user_id", 0);
		project_id = prefs.getInt("project_id", 0);
		
		p_user_level_id = prefs.getInt("p_user_level_id", 0);
		
		//newly added views
		listView = (ListView) findViewById(R.id.listView1);
		btn_addComment = (Button) findViewById(R.id.btn_taskviewAddComment);
		edt_addComment = (EditText) findViewById(R.id.edt_taskviewAddComment);
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView commentor_id = (TextView) view.findViewById(R.id.txt_commentRowAuthorId);
				final TextView comment_id = (TextView) view.findViewById(R.id.txt_commentRowId);
				final TextView comment_content = (TextView) view.findViewById(R.id.txt_commonRowContent);
				
				if(Integer.parseInt(commentor_id.getText().toString())== user_id ||  p_user_level_id == 1){
					AlertDialog.Builder adb1 = new AlertDialog.Builder(TaskView.this);
					final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(TaskView.this,
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
										TaskView.this);
	                            //builderInner.setMessage("Edit your comment here");
	                            builderInner.setTitle("Update comment");
	                            final EditText edittext= new EditText(TaskView.this);
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
		                                        	InputMethodManager inputManager = (InputMethodManager)
		                                                    getSystemService(Context.INPUT_METHOD_SERVICE); 

		                        					inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
		                                                       InputMethodManager.HIDE_NOT_ALWAYS);
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
	                        			TaskView.this);
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
		
		cal = Calendar.getInstance();
		dateFormatter = new SimpleDateFormat("EEE, d MMMM yyyy");
		dateFinalFormatter = new SimpleDateFormat("yyyy-MM-dd");
		
		datepicker_dueDate = new DatePickerDialog(TaskView.this, new OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				
				Calendar newCal = Calendar.getInstance();
				newCal.set(year, monthOfYear, dayOfMonth);
				due_date = dateFinalFormatter.format(newCal.getTime());
				btn_deadline.setText(dateFormatter.format(newCal.getTime()));
			}
		}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		
		task_count = getIntent().getIntExtra("task_count", 0);
		
		try {
			task_id = getIntent().getIntExtra("task_id", 0);
			task_name = getIntent().getStringExtra("task_name");
			task_date = getIntent().getStringExtra("task_date");
			task_status = getIntent().getStringExtra("task_status");
			task_isLate = getIntent().getStringExtra("task_isLate");
			task_assignee = getIntent().getStringExtra("task_assignee");
		} catch (Exception e) {
			// TODO: handle exception
			finish();
			Toast.makeText(getBaseContext(), "Lost connection to the server", Toast.LENGTH_SHORT).show();
			
		}
		
		edt_addComment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edt_addComment.setError(null);
			}
		});
		
		btn_addComment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(edt_addComment.getText().toString().trim().equals("")){
					edt_addComment.setError("You cannot post a blank comment");
				}else{
					toPostComment = true;
					toCommentContent = edt_addComment.getText().toString();
					
					InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE); 

					inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                               InputMethodManager.HIDE_NOT_ALWAYS);
					edt_addComment.setText("");
					new NetCheck().execute();
				}
			}
		});
		
		
		txt_top.setText("This task belongs to the goal: ..");
		txt_taskStatus.setText("Task status: "+task_status);
		
		cb_task.setText(task_name);
		if (task_status.equalsIgnoreCase("Done")){
			cb_task.setChecked(true);
			btn_assign.setEnabled(false);
			btn_deadline.setEnabled(false);
		}else{
			cb_task.setChecked(false);
			btn_assign.setEnabled(true);
			btn_deadline.setEnabled(true);
		}
		
		cb_task.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(task_status.equals("Done")){
					AlertDialog.Builder adb = new AlertDialog.Builder(TaskView.this);
					adb.setTitle("Reopen Task");
					adb.setMessage("This task is currently completed. Do you want to reopen it?");
					adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							toUpdate = true;
							new NetCheck().execute();
						}
					});
					adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							cb_task.setChecked(true);
						}
					});
					adb.show();
				}else{
					toUpdate = true;
					new NetCheck().execute();
				}
			}
		});
		
		btn_startTask.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder adb = new AlertDialog.Builder(TaskView.this);
				adb.setTitle("Start Task");
				adb.setMessage("Do you want to start this task?");
				adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						toStart = true;
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
		
		
		txt_taskDate.setText(task_date);
		if(task_isLate.equalsIgnoreCase("Late")){
			if(task_status.equalsIgnoreCase("Done")){
				txt_taskDate.setTextColor(Color.DKGRAY);
			}else{
				txt_taskDate.setTextColor(Color.RED);
			}
		}else{
			txt_taskDate.setTextColor(Color.DKGRAY);
		}
		
		if(task_status.equalsIgnoreCase("Open")){
			btn_startTask.setVisibility(View.VISIBLE);
		}else if (task_status.equalsIgnoreCase("In progress")) {
			btn_startTask.setVisibility(View.GONE);
		}else{
			btn_startTask.setVisibility(View.GONE);
		}
		
		txt_taskAssignment.setText("Assigned to "+task_assignee);
		edt_taskName.setText(task_name);
		
		btn_deadline.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				datepicker_dueDate.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis()+( 1000 * 60 * 60 * 24 ));
				
				
				Calendar x = Calendar.getInstance();
				try {
					x.setTime(dateFinalFormatter.parse(project_deadline));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					datepicker_dueDate.getDatePicker().setMaxDate(x.getTimeInMillis());
					
					datepicker_dueDate.show();
				} catch (Exception e) {
					AlertDialog.Builder adb = new AlertDialog.Builder(TaskView.this);
					adb.setTitle("Deadline Error");
					adb.setMessage("Task due date cannot be extended. Project deadline already reached");
					adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					});
					adb.show();
				}
			}
		});
		
		
		btn_assign.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builderSingle = new AlertDialog.Builder(TaskView.this);
	            builderSingle.setIcon(R.drawable.ic_launcher);
	            //builderSingle.setTitle("Select One Name:-");
	            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(TaskView.this,
	                    android.R.layout.select_dialog_item);
	            arrayAdapter.add("Anyone");
	            for(int i = 0; i < membersId.size(); i++){
	            	arrayAdapter.add(membersName.get(i));
	            }
	            
	            


	            builderSingle.setAdapter(arrayAdapter,
	                    new DialogInterface.OnClickListener() {

	                        @Override
	                        public void onClick(DialogInterface dialog, int which) {
	                            String strName = arrayAdapter.getItem(which);
	                            if (which == 0){
	                            	assignId = 0;
	                            }else{
	                            	assignId = Integer.parseInt(membersId.get(which-1));
	                            }
	                            btn_assign.setText(strName);
	                            //Toast.makeText(getBaseContext(), ""+assignId, Toast.LENGTH_SHORT).show();
	                        }
	                    });
	            builderSingle.show();
			}
		});
		edt_taskName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edt_taskName.setError(null);
			}
		});
		
		btn_save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(edt_taskName.getText().toString().trim().equals("")){
					edt_taskName.setError("You must name this task!");
				}else{
					toSave=true;
					new NetCheck().execute();
				}
			}
		});
		new NetCheck().execute();
		
		
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		new NetCheck().execute();
		super.onRestart();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.task_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_task_delete:
			if(task_count==1){
				AlertDialog.Builder adb = new AlertDialog.Builder(TaskView.this);
				adb.setTitle("Delete Task");
				adb.setMessage("This is the only task in its goal. Removing it would also mean removing the goal from the project. Proceed?");
				adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						toDelete = true;
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
				return true;
			}else{
				AlertDialog.Builder adb = new AlertDialog.Builder(TaskView.this);
				adb.setTitle("Delete Task");
				adb.setMessage("Are you sure you want to delete this task?");
				adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						toDelete = true;
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
				return true;
			}
		case R.id.action_task_edit:
			Intent i = new Intent(TaskView.this, TaskEdit.class);
			i.putExtra("task_id", task_id);
			i.putExtra("task_status", task_status);
			startActivity(i);
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
                
                
				
				
				if (toSave==true){
					toSave=false;
					new saveTask().execute();
				}else if (toUpdate==true) {
					toUpdate = false;
					new toUpdateTask().execute();
				}else if (toDelete==true) {
					toDelete = false;
					new deleteTask().execute();
				}else if (toStart==true) {
					toStart = false;
					new toStartTask().execute();
				}else if (toPostComment==true) {
					toPostComment = false;
					new postComment().execute();
				}else if (toUpdateComment) {
					toUpdateComment = false;
					new UpdateComment().execute();
				}else if (toDeleteComment==true) {
					toDeleteComment = false;
					new DeleteComment().execute();
					
				}else {
					
					new getProjectMembersForAssign().execute();
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
			
			Log.e("Comment to be posted", toCommentContent);
			JSONObject json = uf.postCommentTask(user_id, task_id, toCommentContent);
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
	
	public class getProjectMembersForAssign extends AsyncTask<Void, Void, JSONObject>{
		
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.e("huh",""+project_id);
			userFunctions uf = new userFunctions();
			JSONObject json = uf.getProjectMembersForAssign(project_id);
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					 
					members= json.getJSONArray("membersName");
					membersId.clear();
					membersName.clear();
					
						for(int i=0; i<members.length();i++){
							JSONObject x = members.getJSONObject(i);
							
							String temp_id = x.getString("user_id");
							String temp_name = x.getString("full_name");
							TaskView.this.membersId.add(temp_id);
							TaskView.this.membersName.add(temp_name);
						}
						
						Toast.makeText(getBaseContext(), membersId.toString(), Toast.LENGTH_SHORT);
					
						
						new getTaskInfo().execute();
					
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					Toast.makeText(getBaseContext(), "Unable to connect to the network", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	public class deleteTask extends AsyncTask<Void, Void, JSONObject>{
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.e("huh",""+project_id);
			userFunctions uf = new userFunctions();
			JSONObject json = uf.deleteTask(user_id, task_id);
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					 
					
					Toast.makeText(getBaseContext(), "Task deleted", Toast.LENGTH_SHORT).show();
					finish();
		
			
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
	
	public class saveTask extends AsyncTask<Void, Void, JSONObject>{
		String newTaskName;
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			newTaskName = edt_taskName.getText().toString().trim();
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.e("huh",""+project_id);
			userFunctions uf = new userFunctions();
			JSONObject json = uf.saveTask(user_id, task_id, newTaskName, assignId, due_date );
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					 
					
					Toast.makeText(getBaseContext(), "You have successfully updated the task", Toast.LENGTH_SHORT).show();
					finish();
		
			
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
	public class toUpdateTask extends AsyncTask<Void, Void, JSONObject>{
		//String newTaskName;
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			//newTaskName = edt_taskName.getText().toString().trim();
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.e("huh",""+project_id);
			userFunctions uf = new userFunctions();
			JSONObject json = uf.updateTask(user_id, goal_id, task_id);
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					 
					
					Toast.makeText(getBaseContext(), "You have successfully updated the task", Toast.LENGTH_SHORT).show();
					finish();
		
			
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
	
	public class toStartTask extends AsyncTask<Void, Void, JSONObject>{
		//String newTaskName;
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			//newTaskName = edt_taskName.getText().toString().trim();
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.e("huh",""+project_id);
			userFunctions uf = new userFunctions();
			JSONObject json = uf.startTask(user_id, goal_id, task_id);
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					 
					
					Toast.makeText(getBaseContext(), "You have successfully started the task", Toast.LENGTH_SHORT).show();
					finish();
		
			
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
	
	
	public class getTaskInfo extends AsyncTask<Void, Void, JSONObject>{
		ProgressDialog pd;
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			pd = new ProgressDialog(TaskView.this);
			pd.setMessage("Loading task info..");
			pd.setIndeterminate(false);
			pd.setCancelable(false);
			pd.show();
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.e("huh",""+project_id);
			userFunctions uf = new userFunctions();
			JSONObject json = uf.getTaskInfo(task_id);
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					
					 pd.dismiss();
					assignId = json.getInt("assignee_id");
					
					try {
						due_date = dateFinalFormatter.format(dateFinalFormatter.parse(json.getString("date_due")));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				    cb_task.setText(json.getString("new_task_name"));
					goal_id = json.getInt("goal_id");
					txt_top.setText("This task belongs to the goal: "+json.getString("goal_name"));
					txt_taskCreator.setText("Added by "+json.getString("creator"));
					
					if (assignId==0){
						btn_assign.setText("Anyone");
					}else{
						Log.e("iddd",""+assignId+" | "+membersId.indexOf(assignId));
						Log.e("arrr",membersId.toString());
						btn_assign.setText(membersName.get(membersId.indexOf(""+assignId)));
					}
					
					
					try {
						btn_deadline.setText(dateFormatter.format(dateFinalFormatter.parse(due_date)));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						project_deadline = dateFinalFormatter.format(dateFinalFormatter.parse(json.getString("project_deadline")));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					new getTaskComments().execute();
				
				}else{
					pd.dismiss();
					
					
					Toast.makeText(getBaseContext(), "Unable to connect to the network", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	
	public class getTaskComments extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
			JSONObject json = uf.viewTaskComments(task_id);
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
			    	TaskView.this.c_id.clear();
			    	TaskView.this.c_author.clear();
			    	TaskView.this.c_author_id.clear();
			    	TaskView.this.c_content.clear();
			    	TaskView.this.c_date.clear();
			    	TaskView.this.c_avatar.clear();
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
							
							TaskView.this.c_id.add(c_id);
							TaskView.this.c_author.add(c_author);
							TaskView.this.c_author_id.add(c_author_id);
							TaskView.this.c_content.add(c_content);
							TaskView.this.c_date.add(c_date);
							
							
							byte[] blobimg1 = Base64.decode(avatar_path.getBytes(), 1);
					    	bitmap = BitmapFactory.decodeByteArray(blobimg1, 0, blobimg1.length);
							//Log.e("bitmap2", ""+bitmap2);
					    	TaskView.this.c_avatar.add(bitmap);
					    	
					    	
							
						}
			    	}else{
			    		
			    		hasComments = false;
			    		
			    		
			    	}
			    	
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							
							
						    
						    if(hasComments==true){
						    	CommentsAdapter adapter = new CommentsAdapter(getApplicationContext(), c_id, c_author,c_author_id, c_content, c_date, c_avatar);
						    	listView.setAdapter(adapter);
						    	
						    	listView.setEmptyView(findViewById(R.id.no_comments_on_task));
						    	adapter.notifyDataSetChanged();
						    	//ListUtils.setDynamicHeight(lv_comments);
						    	
						    	
						    }else{
						    	CommentsAdapter adapter = new CommentsAdapter(getApplicationContext(), c_id, c_author,c_author_id, c_content, c_date, c_avatar);
						    	listView.setAdapter(adapter);
						    	listView.setEmptyView(findViewById(R.id.no_comments_on_task));
						    	
						    	adapter.notifyDataSetChanged();
						    	//ListUtils.setDynamicHeight(lv_comments);
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
						Intent i = new Intent(TaskView.this, DashboardMe.class);
						i.putExtra("user_id", c_author_id.get(position));
						i.putExtra("profile_name",c_author.get(position));
						startActivity(i);
					}
				});
				
				holder.img_pic.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent i = new Intent(TaskView.this, DashboardMe.class);
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
}
