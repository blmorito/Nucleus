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

import com.example.nucleus.ProjectMembers.getProjectMembers;
import com.example.nucleus.ProjectMembers.projectMembersAdapter;
import com.example.nucleus.ProjectMembers.projectMembersAdapter.myViewHolder;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Discussion extends Activity {
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	
	SharedPreferences prefs;
	Integer user_id, workspace_id, ws_user_level_id, project_id, p_user_level_id;
	ListView lv_discussion;
	
	LayoutInflater inflater;
	//Bitmap bitmap;
	JSONArray discussions;
	Boolean toDelete;
	Integer toDeleteId;
	
	String sortType;
	
	public List<String> discId = new ArrayList<String>();
	public List<String> discSubject = new ArrayList<String>();
	public List<String> discBody = new ArrayList<String>();
	public List<String> discAuthor = new ArrayList<String>();
	public List<String> discAuthorId = new ArrayList<String>();
	public List<String> discAgo = new ArrayList<String>();
	public List<String> discCommentCount = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.discussion);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		sortType = "newest";
		prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
		
		user_id =  prefs.getInt("user_id", 0);
		workspace_id = prefs.getInt("workspace_id", 0);
		project_id = prefs.getInt("project_id", 0);
		p_user_level_id = prefs.getInt("p_user_level_id", 0);
		lv_discussion = (ListView) findViewById(R.id.listViewDiscussion);
		//lv_discussion.setEmptyView(findViewById(R.id.no_discussion));
		new NetCheck().execute();
		toDelete = false;
		toDeleteId = 0;
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		lv_discussion.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				TextView discuss_id = (TextView) view.findViewById(R.id.txt_discussionId);
				TextView discuss_author = (TextView) view.findViewById(R.id.txt_discussionAuthor);
				TextView discuss_subject = (TextView) view.findViewById(R.id.txt_discussionSubject);
				TextView discuss_body = (TextView) view.findViewById(R.id.txt_discussionBody);
				
				Intent i = new Intent(Discussion.this, DiscussionView.class);
				i.putExtra("discuss_id", discuss_id.getText().toString());
				i.putExtra("discuss_author", discuss_author.getText().toString());
				i.putExtra("discuss_subject", discuss_subject.getText().toString());
				i.putExtra("discuss_body", discuss_body.getText().toString());
				
				startActivity(i);
				
			}
		});
		
		lv_discussion.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				TextView idd = (TextView) view.findViewById(R.id.txt_discussionAuthorId);
				final TextView d_id = (TextView) view.findViewById(R.id.txt_discussionId);
				final TextView d_subject = (TextView) view.findViewById(R.id.txt_discussionSubject);
				final TextView d_body = (TextView) view.findViewById(R.id.txt_discussionBody);
				
				if (Integer.parseInt(idd.getText().toString())== user_id || p_user_level_id!=3){
					//Toast.makeText(getBaseContext(), "Can delete this shit", Toast.LENGTH_SHORT).show();
					AlertDialog.Builder builderSingle = new AlertDialog.Builder(Discussion.this);
		            builderSingle.setIcon(R.drawable.ic_launcher);
		            //builderSingle.setTitle("Select One Name:-");
		            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Discussion.this,
		                    android.R.layout.select_dialog_item);
		            arrayAdapter.add("Edit discussion");
		            arrayAdapter.add("Delete this discussion");


		            builderSingle.setAdapter(arrayAdapter,
		                    new DialogInterface.OnClickListener() {

		                        @Override
		                        public void onClick(DialogInterface dialog, int which) {
		                            String strName = arrayAdapter.getItem(which);
		                            if(strName.equals("Delete this discussion")){
		                            	AlertDialog.Builder builderInner = new AlertDialog.Builder(
			                                    Discussion.this);
			                            builderInner.setMessage("Are you sure you want to delete this discussion?");
			                            builderInner.setTitle("Confirmation");
			                            builderInner.setPositiveButton("Yes",
			                                    new DialogInterface.OnClickListener() {

			                                        @Override
			                                        public void onClick( DialogInterface dialog,int which) {
			                                        	toDeleteId = Integer.parseInt(d_id.getText().toString());
			                                        	toDelete = true;
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
		                            }else{
		                            	Intent i = new Intent (Discussion.this, DiscussionEdit.class);
		                            	i.putExtra("discussion_id", d_id.getText().toString());
		                            	i.putExtra("discussion_subject", d_subject.getText().toString());
		                            	i.putExtra("discussion_body", d_body.getText().toString());
		                            	startActivity(i);
		                            }
		                        }
		                    });
		            builderSingle.show();
				}else{
					//Toast.makeText(getBaseContext(), "Only the author of this shit can delete or the leader himself fucked up", Toast.LENGTH_SHORT).show();
				}
				return false;
				
			}
		});
		
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
		inflater.inflate(R.menu.discussion_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_discussion_new:
			startActivity(new Intent(Discussion.this, DiscussionNew.class));
			finish();
			return true;
		case R.id.action_discussion_sortNew:
			sortType = "newest";
			new NetCheck().execute();
			return true;
		case R.id.action_discussion_sortOld:
			sortType = "oldest";
			new NetCheck().execute();
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
                
                
              
                if (toDelete==true){
                	toDelete = false;
                	
                	new DeleteDiscussion().execute();
                }else{
                	new getDiscussions().execute();
                }
               //new getProjectMembers().execute();
            }
            else{
            	setProgressBarIndeterminateVisibility(false);
            	
            	Toast.makeText(getBaseContext(), "Unable to connect to the network", Toast.LENGTH_SHORT).show();
            	
                
                
            }
		}
		
	}
	
	public class getDiscussions extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
			JSONObject json = uf.getDiscussions(project_id, sortType);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					discussions = json.getJSONArray("discussions");
					Discussion.this.discId.clear();
					Discussion.this.discAuthor.clear();
					Discussion.this.discAuthorId.clear();
					Discussion.this.discAgo.clear();
					Discussion.this.discBody.clear();
					Discussion.this.discCommentCount.clear();
					Discussion.this.discSubject.clear();
					
					
					for(int i=0; i< discussions.length(); i++){
						JSONObject z = discussions.getJSONObject(i);
						
						String disc_id = ""+z.getInt("discussion_id");
						String disc_author = z.getString("discussion_author");
						String disc_author_id = z.getString("discussion_author_id");
						String disc_ago = z.getString("ago");
						String disc_subject = z.getString("subject");
						String disc_body = z.getString("body");
						String disc_num = ""+z.getInt("discussion_num_comments");
						
						
						
						Discussion.this.discId.add(disc_id);
						Discussion.this.discAuthor.add(disc_author);
						Discussion.this.discAuthorId.add(disc_author_id);
						Discussion.this.discBody.add(disc_body);
						Discussion.this.discSubject.add(disc_subject);
						Discussion.this.discAgo.add(disc_ago);
						Discussion.this.discCommentCount.add(disc_num);
						
						
						
						
						
						
					}
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							
							//wsShowMembers.setVisibility(LinearLayout.VISIBLE);
							//loader.setVisibility(LinearLayout.GONE);
							discussionsAdapter adapter = new discussionsAdapter(getApplicationContext(), discId, discSubject, discBody, discAuthor,discAuthorId, discAgo, discCommentCount);
							lv_discussion.setAdapter(adapter);
							lv_discussion.setEmptyView(findViewById(R.id.no_discussion));
							adapter.notifyDataSetChanged();
							
						}
					});
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					Discussion.this.discId.clear();
					Discussion.this.discAuthor.clear();
					Discussion.this.discAuthorId.clear();
					Discussion.this.discAgo.clear();
					Discussion.this.discBody.clear();
					Discussion.this.discCommentCount.clear();
					Discussion.this.discSubject.clear();
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							
							discussionsAdapter adapter = new discussionsAdapter(getApplicationContext(), discId, discSubject, discBody, discAuthor,discAuthorId, discAgo, discCommentCount);
							lv_discussion.setAdapter(adapter);
							lv_discussion.setEmptyView(findViewById(R.id.no_discussion));
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
	public class DeleteDiscussion extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
			JSONObject json = uf.deleteDiscussion(toDeleteId);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					Toast.makeText(getBaseContext(), "Successfully deleted a discussion", Toast.LENGTH_SHORT).show();
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
	
	class discussionsAdapter extends ArrayAdapter<String>{
		Context context;
		List<String> discIdX;
		List<String> discSubjectX;
		List<String> discBodyX;
		List<String> discAuthorX;
		List<String> discAuthorIdX;
		List<String> discAgoX;
		List<String> discCommentCountX;
		
		discussionsAdapter(Context c, List<String> discId, List<String> discSubject, List<String> discBody, List<String> discAuthor,List<String> discAuthorId, List<String> discAgo, List<String> discCommentCount){
			super(c,R.layout.discussion_row,R.id.txt_discussionId, discId);
			this.context = c;
			this.discIdX = discId;
			this.discSubjectX = discSubject;
			this.discBodyX = discBody;
			this.discAuthorX = discAuthor;
			this.discAuthorIdX = discAuthorId;
			this.discAgoX = discAgo;
			this.discCommentCountX = discCommentCount;
		}
		
		class myViewHolder{
			TextView txt_id, txt_subject, txt_body, txt_author,txt_authorId, txt_ago, txt_count;
			
			View viewz;
			
			
			public myViewHolder(View view) {
				txt_id = (TextView) view.findViewById(R.id.txt_discussionId);
				txt_subject = (TextView) view.findViewById(R.id.txt_discussionSubject);
				txt_body = (TextView) view.findViewById(R.id.txt_discussionBody);
				txt_author = (TextView) view.findViewById(R.id.txt_discussionAuthor);
				txt_authorId = (TextView) view.findViewById(R.id.txt_discussionAuthorId);
				txt_ago = (TextView) view.findViewById(R.id.txt_discussionAgo);
				txt_count = (TextView) view.findViewById(R.id.txt_discussionCommentCount);
				viewz = (View) view.findViewById(R.id.view2);
				
				
				
				
				
				
				
			}
			
			
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			View row = convertView;
			myViewHolder holder = null;
			
			if (row == null){
				inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.discussion_row, parent, false);
				holder = new myViewHolder(row);
				row.setTag(holder);
			}else{
				holder = (myViewHolder) row.getTag();
			}
			
			
				holder.txt_id.setText(discIdX.get(position));
				holder.txt_subject.setText(discSubjectX.get(position));
				holder.txt_body.setText(discBodyX.get(position));
				holder.txt_author.setText(discAuthorX.get(position));
				holder.txt_authorId.setText(discAuthorIdX.get(position));
				holder.txt_ago.setText(discAgoX.get(position));
				
				
				if(discCommentCountX.get(position).equals("0")){
					holder.txt_count.setVisibility(View.GONE);
					holder.viewz.setVisibility(View.GONE);
				}else{
					holder.txt_count.setVisibility(View.VISIBLE);
					holder.viewz.setVisibility(View.VISIBLE);
					if(discCommentCountX.get(position).equals("1")){
						holder.txt_count.setText(discCommentCountX.get(position)+" comment");
					}else{
						holder.txt_count.setText(discCommentCountX.get(position)+" comments");
					}
				}
				
				//holder.image.setImageBitmap(getRoundedShape(avatarsX.get(position)));
				
				
				
			
			
			return row;
		}
	}
	
}
