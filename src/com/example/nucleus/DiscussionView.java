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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DiscussionView extends Activity {
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	SharedPreferences prefs;
	Integer user_id, workspace_id, project_id, p_user_level_id, ws_user_level_id, discussion_id;
	LayoutInflater inflater;
	ImageView img_pic;
	TextView txt_author, txt_date, txt_subject, txt_body, txt_authorId;
	EditText edt_comment;
	Button btn_postComment;
	Bitmap bitmap;
	Bitmap bitmap2;
	ListView lv_comments;
	JSONArray comments;
	String myComment;
	
	//flags
	int temp, toDeleteId, toDeleteCommentId, toUpdateCommentId;
	String toUpdateCommentContent;
	
	public List<String> c_id = new ArrayList<String>();
	public List<String> c_author = new ArrayList<String>();
	public List<String> c_author_id = new ArrayList<String>();
	public List<String> c_content = new ArrayList<String>();
	public List<String> c_date = new ArrayList<String>();
	public List<Bitmap> c_avatar = new ArrayList<Bitmap>();
	
	Boolean toComment;
	Boolean toDelete;
	Boolean toDeleteComment;
	Boolean toUpdateComment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.discussion_view);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
		toComment = false;
		toDelete = false;
		toDeleteComment = false;
		toUpdateComment = false;
		myComment = "";
		//for prefs
		
		user_id = prefs.getInt("user_id", 0);
		p_user_level_id = prefs.getInt("p_user_level_id", 0);
		//end
		
		
		edt_comment = (EditText) findViewById(R.id.edt_discussViewComment);
		btn_postComment = (Button) findViewById(R.id.btn_discussViewPost);
		lv_comments = (ListView) findViewById(R.id.lv_discussView);
		
		View header = getLayoutInflater().inflate(R.layout.testhead, null);
		img_pic = (ImageView) header.findViewById(R.id.img_discussView);
		txt_author = (TextView) header.findViewById(R.id.txt_discussViewAuthor);
		txt_date = (TextView) header.findViewById(R.id.txt_discussViewDate);
		txt_subject = (TextView) header.findViewById(R.id.txt_discussViewSubject);
		txt_body = (TextView) header.findViewById(R.id.txt_discussViewBody);
		txt_authorId = (TextView) header.findViewById(R.id.txt_headId);
		lv_comments.addHeaderView(header);
		Bundle extras = getIntent().getExtras();
		if (extras != null){
			
			txt_author.setText(extras.getString("discuss_author"));
			txt_subject.setText(extras.getString("discuss_subject"));
			txt_body.setText(extras.getString("discuss_body"));
			discussion_id = Integer.parseInt(extras.getString("discuss_id"));
		}
		
		setTitle(txt_subject.getText().toString());
		
		new NetCheck().execute();
		
		edt_comment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edt_comment.setError(null);
			}
		});
		
		btn_postComment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (edt_comment.getText().toString().trim().equals("")){
					edt_comment.setError("Blank comments are not allowed");
				}else{
					toComment = true;
					myComment = edt_comment.getText().toString().trim();
					new NetCheck().execute();
					
					edt_comment.setText("");
					 InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			         imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
				
				}
			}
		});
		
		img_pic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(DiscussionView.this, DashboardMe.class);
				i.putExtra("user_id", txt_authorId.getText().toString());
				i.putExtra("profile_name", txt_author.getText().toString());
				startActivity(i);
			}
		});
		
		lv_comments.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				TextView com_author_id = (TextView) view.findViewById(R.id.txt_commentRowAuthorId);
				final TextView com_id = (TextView) view.findViewById(R.id.txt_commentRowId);
				final TextView com_content = (TextView) view.findViewById(R.id.txt_commonRowContent);
				
				if(Integer.parseInt(com_author_id.getText().toString())== user_id || p_user_level_id!=3){
					AlertDialog.Builder builderSingle = new AlertDialog.Builder(DiscussionView.this);
		            builderSingle.setIcon(R.drawable.ic_launcher);
		            //builderSingle.setTitle("Select One Name:-");
		            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(DiscussionView.this,
		                    android.R.layout.select_dialog_item);
		            arrayAdapter.add("Edit comment");
		            arrayAdapter.add("Delete this comment");


		            builderSingle.setAdapter(arrayAdapter,
		                    new DialogInterface.OnClickListener() {

		                        @Override
		                        public void onClick(DialogInterface dialog, int which) {
		                            String strName = arrayAdapter.getItem(which);
		                            if(strName.equals("Delete this comment")){
		                            	AlertDialog.Builder builderInner = new AlertDialog.Builder(
		                            			DiscussionView.this);
			                            builderInner.setMessage("Are you sure you want to delete this comment?");
			                            builderInner.setTitle("Confirmation");
			                            builderInner.setPositiveButton("Yes",
			                                    new DialogInterface.OnClickListener() {

			                                        @Override
			                                        public void onClick( DialogInterface dialog,int which) {
			                                        	toDeleteCommentId = Integer.parseInt(com_id.getText().toString());
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
		                            }else{
		                            	AlertDialog.Builder builderInner = new AlertDialog.Builder(
		                            			DiscussionView.this);
			                            //builderInner.setMessage("Edit your comment here");
			                            builderInner.setTitle("Update comment");
			                            final EditText edittext= new EditText(DiscussionView.this);
			                            edittext.setOnClickListener(new OnClickListener() {
											
											@Override
											public void onClick(View v) {
												// TODO Auto-generated method stub
												edittext.setError(null);
											}
										});
			                            builderInner.setView(edittext);
			                            String timeStamp = new SimpleDateFormat("EEE, d MMM yyyy 'at' h:mm a").format(Calendar.getInstance().getTime());
			                            edittext.setText(com_content.getText().toString());
			                            builderInner.setPositiveButton("Update",
			                                    new DialogInterface.OnClickListener() {

			                                        @Override
			                                        public void onClick( DialogInterface dialog,int which) {
			                                        	if (edittext.getText().toString().trim().equals("")){
			                                        		edittext.setError("You cannot post a blank comment");
			                                        	}else{
			                                        		toUpdateComment = true;
				                                        	toUpdateCommentId = Integer.parseInt(com_id.getText().toString());
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
		                            }
		                        }
		                    });
		            builderSingle.show();
				}
				return false;
			}
		});
		
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.discussion_view_menu
				, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		 
		
		if(p_user_level_id!=3 || temp==user_id){
			
			menu.findItem(R.id.action_viewdiscussion_delete).setVisible(true);
			menu.findItem(R.id.action_viewdiscussion_edit).setVisible(true);
		}else{
			menu.findItem(R.id.action_viewdiscussion_delete).setVisible(false);
			menu.findItem(R.id.action_viewdiscussion_edit).setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		switch (item.getItemId()) {
		case R.id.action_viewdiscussion_edit:
			Intent i = new Intent (DiscussionView.this, DiscussionEdit.class);
        	i.putExtra("discussion_id", ""+discussion_id);
        	i.putExtra("discussion_subject", txt_subject.getText().toString());
        	i.putExtra("discussion_body", txt_body.getText().toString());
        	startActivity(i);
			return true;
		case R.id.action_viewdiscussion_delete:
			AlertDialog.Builder builderInner = new AlertDialog.Builder(
                    DiscussionView.this);
            builderInner.setMessage("Are you sure you want to delete this discussion?");
            builderInner.setTitle("Confirmation");
            builderInner.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick( DialogInterface dialog,int which) {
                        	toDeleteId = discussion_id;
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
			return true;
		case R.id.action_viewdiscussion_activity:
	
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
                
                
				
               if (toComment==true){
            	   toComment = false;
            	   new postCommentDiscussion().execute();
               }else if (toDelete) {
            	   toDelete = false;
            	   new deleteDiscussion().execute();
					
			   }else if (toDeleteComment) {
				   toDeleteComment = false;
				   new deleteCommentInDiscussion().execute();
			   }else if (toUpdateComment) {
				   toUpdateComment=false;
				   new updateCommentInDiscussion().execute();
			   }else{
            	   new viewDiscussion().execute();
               }
                
            }
            else{
            	setProgressBarIndeterminateVisibility(false);


            	
            	
            	Toast.makeText(getBaseContext(), "Couldn't connect to the network", Toast.LENGTH_SHORT).show();
                
                
            }
		}
		
	}	
	
	public class viewDiscussion extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
			JSONObject json = uf.viewDiscussion(discussion_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					final int a_id = json.getInt("author_id");
					
					final String d_date = json.getString("date_posted");
					String d_pic = json.getString("avatar_img");
					
					byte[] blobimg = Base64.decode(d_pic.getBytes(), 1);
			    	bitmap = BitmapFactory.decodeByteArray(blobimg, 0, blobimg.length);
			    	
			    	final Boolean hasComments;
			    	//get comments
			    	DiscussionView.this.c_id.clear();
			    	DiscussionView.this.c_author.clear();
			    	DiscussionView.this.c_author_id.clear();
			    	DiscussionView.this.c_content.clear();
			    	DiscussionView.this.c_date.clear();
			    	DiscussionView.this.c_avatar.clear();
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
							
							DiscussionView.this.c_id.add(c_id);
					    	DiscussionView.this.c_author.add(c_author);
					    	DiscussionView.this.c_author_id.add(c_author_id);
					    	DiscussionView.this.c_content.add(c_content);
					    	DiscussionView.this.c_date.add(c_date);
							
							
							byte[] blobimg1 = Base64.decode(avatar_path.getBytes(), 1);
					    	bitmap2 = BitmapFactory.decodeByteArray(blobimg1, 0, blobimg1.length);
							Log.e("bitmap2", ""+bitmap2);
					    	DiscussionView.this.c_avatar.add(bitmap2);
					    	
					    	
							
						}
			    	}else{
			    		
			    		hasComments = false;
			    		
			    		
			    	}
			    	
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							
							txt_authorId.setText(""+a_id);
							temp = Integer.parseInt(txt_authorId.getText().toString().trim());
							invalidateOptionsMenu();
						    txt_date.setText(d_date);
						    img_pic.setImageBitmap(getRoundedShape(bitmap));
						    
						    if(hasComments==true){
						    	CommentsAdapter adapter = new CommentsAdapter(getApplicationContext(), c_id, c_author,c_author_id, c_content, c_date, c_avatar);
						    	lv_comments.setAdapter(adapter);
						    	//lv_comments.setEmptyView(findViewById(R.id.no_comments));
						    	adapter.notifyDataSetChanged();
						    	
						    	
						    }else{
						    	CommentsAdapter adapter = new CommentsAdapter(getApplicationContext(), c_id, c_author,c_author_id, c_content, c_date, c_avatar);
						    	lv_comments.setAdapter(adapter);
						    	
						    	adapter.notifyDataSetChanged();
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
   public class deleteDiscussion extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
   
   public class deleteCommentInDiscussion extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
   public class updateCommentInDiscussion extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
	
	public class postCommentDiscussion extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
			JSONObject json = uf.postCommentDiscussion(user_id, discussion_id, myComment);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					Toast.makeText(getBaseContext(), "You have successfully posted a comment", Toast.LENGTH_SHORT).show();
			    	new NetCheck().execute();
			    	
					
					
				}else{
					setProgressBarIndeterminateVisibility(false);
					Toast.makeText(getBaseContext(), json.getString("message"), Toast.LENGTH_SHORT);
					
					
					//Toast.makeText(getBaseContext(), "Couldnt retrieve information", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		new NetCheck().execute();
		super.onRestart();
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
						Intent i = new Intent(DiscussionView.this, DashboardMe.class);
						i.putExtra("user_id", c_author_id.get(position));
						i.putExtra("profile_name",c_author.get(position));
						startActivity(i);
					}
				});
				
				holder.img_pic.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent i = new Intent(DiscussionView.this, DashboardMe.class);
						i.putExtra("user_id", c_author_id.get(position));
						i.putExtra("profile_name",c_author.get(position));
						startActivity(i);
					}
				});
				
				
				
			
			
			return row;
		}
	}
	
	
}
