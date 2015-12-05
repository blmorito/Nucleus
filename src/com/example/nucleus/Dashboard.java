package com.example.nucleus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;












import com.example.nucleus.WsShowMembers.wsMembersAdapter;
import com.example.nucleus.WsShowMembers.wsMembersAdapter.myViewHolder;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.text.InputFilter.LengthFilter;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Dashboard extends Activity implements OnItemClickListener {
	
	private DrawerLayout drawerLayout;
	private ListView listView;
	
	private ActionBarDrawerToggle drawerListener;
	private MyAdapter myAdapter;
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	LinearLayout checkConnect;
	Boolean hasConnection = false;
	SharedPreferences pref;
	static String workspace_name;
	static String  myInvites = "0";
	static String  myNotifications = "0";
	static String user_id;
	static Integer workspace_id;
	Integer ws_user_level_id;
	JSONArray projects;
	LayoutInflater inflater;
	ListView lv;
	
	JSONArray activities;
	Bitmap bitmap;
	
	public List<Bitmap> ac_avatar = new ArrayList<Bitmap>();
	public List<String> ac_pname = new ArrayList<String>();
	public List<String> ac_fname = new ArrayList<String>();
	public List<String> ac_content = new ArrayList<String>();
	public List<String> ac_date = new ArrayList<String>();
	
	
	public static List<String> p_id = new ArrayList<String>();
	public static List<String> p_name = new ArrayList<String>();
	public static List<String> p_level = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.dashboard);
		
		String myMenuList [] = getResources().getStringArray(R.array.myMenu);
		
		//sharedpref
		pref = getSharedPreferences("MyPref", MODE_PRIVATE);
		user_id = ""+pref.getInt("user_id", 0);
		workspace_id = pref.getInt("workspace_id", 0);
		ws_user_level_id = pref.getInt("ws_user_level_id", 0);
		invalidateOptionsMenu();
		
		//logout
		boolean finish = getIntent().getBooleanExtra("finish", false);
	    if (finish) {
	    	SharedPreferences.Editor editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
	    	editor.clear();
	    	editor.commit();
	        startActivity(new Intent(Dashboard.this, MainActivity.class));
	        finish();
	        return;
	    }
	    
	    new NetCheck().execute();
	    
		lv = (ListView) findViewById(R.id.listviewActivity);
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		
		loadNav();
		//listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myMenuList));
		listView.setOnItemClickListener(this);
		drawerListener = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer,
				R.string.drawer_open, R.string.drawer_close){
			@Override
			public void onDrawerOpened(View drawerView) {
				// TODO Auto-generated method stub
				super.onDrawerOpened(drawerView);
			}
			
			@Override
			public void onDrawerClosed(View drawerView) {
				// TODO Auto-generated method stub
				super.onDrawerClosed(drawerView);
			}
		};
		
		drawerLayout.setDrawerListener(drawerListener);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		checkConnect = (LinearLayout) findViewById(R.id.no_connection_layout);
		TextView tryAgain = (TextView) findViewById(R.id.tryAgain); 
		
		tryAgain.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new NetCheck().execute();
			}
		});
		
		
		
		
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		new NetCheck().execute();
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//new NetCheck().execute();
	}
	
	private void loadNav() {
		// TODO Auto-generated method stub
		
		myAdapter = new MyAdapter(this);
		listView = (ListView) findViewById(R.id.drawerList);
		listView.setAdapter(myAdapter);
	}

	public class NetCheck extends AsyncTask<Void, Void, Boolean>{
		private ProgressDialog pDialog;
		
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
                
                hasConnection = true;
                runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						checkConnect.setVisibility(LinearLayout.GONE);
						
					}
				});
                
                new getWorkspaceName().execute();
                 
                //new loginAccount().execute();
                //Toast.makeText(getBaseContext(), "Connection Active", Toast.LENGTH_SHORT).show();
                
            }
            else{
            	setProgressBarIndeterminateVisibility(false);
            	hasConnection = false;
            	runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						checkConnect.setVisibility(LinearLayout.VISIBLE);
						
					}
				});
            	
                
                
            }
		}
		
	}
	
	public class getWorkspaceName extends AsyncTask<Void, Void, JSONObject>{
		
		Integer ws_id;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			ws_id =pref.getInt("workspace_id", 0);
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			userFunctions uf = new userFunctions();
			JSONObject json = uf.dashboardGetWSname(ws_id, user_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					myInvites = ""+json.getInt("myInvites");
					myNotifications = ""+json.getInt("myNotifications");
					workspace_name = json.getString("workspace_name");
					
					projects = json.getJSONArray("projects");
					Dashboard.this.p_id.clear();
					Dashboard.this.p_name.clear();
					Dashboard.this.p_level.clear();
					for(int i=0; i< projects.length(); i++){
						JSONObject z = projects.getJSONObject(i);
						
						String project_id = ""+z.getInt("p_id");
						String project_name = z.getString("p_name");
						String project_level = z.getString("p_level");
						
						Dashboard.this.p_id.add(project_id);
						Dashboard.this.p_name.add(project_name);
						Dashboard.this.p_level.add(project_level);
					
					}
					
					loadNav();
					new getWsActivities().execute();
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					if (json.getInt("errorCode")==1){
						
						Toast.makeText(Dashboard.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}else{
						
						Toast.makeText(Dashboard.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + json);
			}
		}
		
	}
	
	public class getWsActivities extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
			JSONObject json = uf.getWorkspaceActivities(workspace_id, user_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					activities = json.getJSONArray("activities");
					
					ac_avatar.clear();
					ac_pname.clear();
					ac_fname.clear();
					ac_date.clear();
					ac_content.clear();
					
					
					for (int i = 0; i<activities.length(); i++){
						
						JSONObject z = activities.getJSONObject(i);
						String pname = z.getString("act_pname");
						String fname = z.getString("act_fname");
						String content = z.getString("act_content");
						String agodate = z.getString("act_date");
						String avatar = z.getString("act_avatar");
						
						ac_pname.add(pname);
						ac_fname.add(fname);
						ac_content.add(content);
						ac_date.add(agodate);
						try {
							byte[] blobimg = Base64.decode(avatar.getBytes(), 1);
					    	bitmap = BitmapFactory.decodeByteArray(blobimg, 0, blobimg.length);
						} catch (OutOfMemoryError e) {
							// TODO: handle exception
						}
						
				    	ac_avatar.add(bitmap);
						
					}
					
					
					
					ActivitiesAdapter adapter = new ActivitiesAdapter(getApplicationContext(), ac_pname, ac_fname, ac_content, ac_date, ac_avatar);
					lv.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					
					
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					if (json.getInt("errorCode")==1){
						
						Toast.makeText(Dashboard.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}else{
						
						Toast.makeText(Dashboard.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + json);
			}
		}
		
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
		drawerListener.syncState();
	}
	


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(myAdapter.wew.get(position).equalsIgnoreCase("Projects")){
			//Toast.makeText(getBaseContext(), "AA", Toast.LENGTH_SHORT).show();
			drawerLayout.closeDrawers();
			startActivity(new Intent(Dashboard.this, WsShowProjects.class));
		}else if (myAdapter.wew.get(position).equalsIgnoreCase("Invites")) {
			drawerLayout.closeDrawers();
			startActivity(new Intent(Dashboard.this, DashboardInvites.class));
			
		}else if (position==1){
			drawerLayout.closeDrawers();
			Intent i = new Intent(Dashboard.this, DbWorkspace.class);
			i.putExtra("workspace_id", workspace_id );
			i.putExtra("user_id", user_id);
			startActivity(i);
		}else if (myAdapter.wew.get(position).equalsIgnoreCase("People")) {
			drawerLayout.closeDrawers();
			startActivity(new Intent(Dashboard.this, WsShowMembersTLayout.class));
		}else if(myAdapter.wew.get(position).equalsIgnoreCase("Dashboard")){
			drawerLayout.closeDrawers();
			new NetCheck().execute();
			
		}else if (myAdapter.wew.get(position).equalsIgnoreCase("Notifications")) {
			startActivity(new Intent(Dashboard.this, Notifications.class));
		}else if(myAdapter.wew.get(position).equalsIgnoreCase("Me")){
			drawerLayout.closeDrawers();
			Intent i = new Intent(Dashboard.this, DashboardMe.class);
			i.putExtra("user_id", user_id);
			Log.e("check passed extra",""+user_id);
			startActivity(i);
			
		}
		
		if(position>6){//6 = num of built in menu, 7 below meaning + 1 since array start at 0 ORYT
			//Toast.makeText(getBaseContext(), ""+p_id.get(position-7), Toast.LENGTH_SHORT).show();
			
			SharedPreferences.Editor editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
            
            editor.putInt("p_user_level_id", Integer.parseInt(p_level.get(position-7)));
            editor.putInt("project_id", Integer.parseInt(p_id.get(position-7)));
            editor.commit();
            
            Intent i = new Intent(Dashboard.this, Project.class);
            i.putExtra("project_id", p_id.get(position-7));
            i.putExtra("project_name", p_name.get(position-7));
            
            startActivity(i);
		}
		
	
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		if(ws_user_level_id==3){
			menu.findItem(R.id.action_create_project).setVisible(false);
		}else{
			menu.findItem(R.id.action_create_project).setVisible(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int id = item.getItemId();
		if(drawerListener.onOptionsItemSelected(item)){
			return true;
		}else if(id == R.id.action_logout){
			logOut();
		}else if (id == R.id.action_create_project) {
			startActivity(new Intent(Dashboard.this, CreateProject.class));
			
		}else if (id == R.id.action_refresh) {
			new NetCheck().execute();
		}else if (id == R.id.action_switch) {
			startActivity(new Intent(Dashboard.this, SwitchWorkspace.class));
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	
	private void logOut() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, Dashboard.class);
	    intent.putExtra("finish", true);
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
	    startActivity(intent);
	    finish();
	    
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		drawerListener.onConfigurationChanged(newConfig);
	}
	
	class ActivitiesAdapter extends ArrayAdapter<String>{
		Context context;
		List<String> ac_pnameX;
		List<String> ac_fnameX;
		List<String> ac_contentX;
		List<String> ac_dateX;
		List<Bitmap> ac_avatarX;
		
		ActivitiesAdapter(Context c, List<String> ac_pname, List<String> ac_fname, List<String> ac_content, List<String> ac_date, List<Bitmap> ac_avatar){
			super(c,R.layout.dashboard_row,R.id.txt_act_rowPName, ac_pname);
			this.context = c;
			this.ac_pnameX = ac_pname;
			this.ac_fnameX = ac_fname;
			this.ac_contentX = ac_content;
			this.ac_dateX = ac_date;
			
			this.ac_avatarX = ac_avatar;
		}
		
		class myViewHolder{
			TextView text_pname, text_fname, text_content, text_date;
			
			ImageView image;
			
			public myViewHolder(View view) {
				text_pname = (TextView) view.findViewById(R.id.txt_act_rowPName);
				text_fname = (TextView) view.findViewById(R.id.txt_act_rowUser);
				text_content = (TextView) view.findViewById(R.id.txt_act_content);
				text_date = (TextView) view.findViewById(R.id.txt_act_time);
				image = (ImageView) view.findViewById(R.id.img_act_upic);
				
				
				
				
				
				
			}
			
			
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			View row = convertView;
			myViewHolder holder = null;
			
			if (row == null){
				inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.dashboard_row, parent, false);
				holder = new myViewHolder(row);
				row.setTag(holder);
			}else{
				holder = (myViewHolder) row.getTag();
			}
			
			
				holder.text_pname.setText(ac_pnameX.get(position));
				holder.text_fname.setText(ac_fnameX.get(position));
				holder.text_content.setText(ac_contentX.get(position));
				holder.text_date.setText(ac_dateX.get(position));
				
				try {
					holder.image.setImageBitmap(getRoundedShape(ac_avatarX.get(position)));
				} catch (Exception e) {
					Log.e("picccc", ac_fnameX.get(position));
				}
				
				
				
			
			
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

class MyAdapter extends BaseAdapter{
	private Context context;
	String myMenu[];
	ArrayList<String> wew; 
//	ArrayList  <String> wew = new ArrayList<String>();
	int [] iconpic = {R.drawable.ic_dashboard,R.drawable.ic_workspace, R.drawable.ic_projects,R.drawable.ic_people,R.drawable.ic_notifications,R.drawable.ic_invites, R.drawable.ic_me};
	
	public MyAdapter(Context context){
		myMenu = context.getResources().getStringArray(R.array.myMenu);
		wew = new ArrayList<String>(Arrays.asList(myMenu));
		
		if(Dashboard.p_id.size()!=0){
			for(int i =0;i<Dashboard.p_id.size(); i++){
				wew.add(Dashboard.p_name.get(i));
			}
		}
//		wew = (ArrayList<String>) Arrays.asList(myMenu);
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return wew.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return wew.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View row = null;
		if (convertView==null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.navdraw_row, parent, false);
		}else{
			row = convertView;
		}
		TextView txtTitle = (TextView) row.findViewById(R.id.nv_title);
		TextView txtCounter = (TextView) row.findViewById(R.id.nv_counter);
		ImageView imgIcon = (ImageView) row.findViewById(R.id.nv_icon);
		
		if (position== 1){
			txtTitle.setText(Html.fromHtml("Workspace<br><small><emphasis><font color='#00ffff'>"+Dashboard.workspace_name+"</font></emphasis></small>"));
			txtCounter.setVisibility(View.GONE);
		}else if (position==5) {
			txtTitle.setText(wew.get(position));
			
			if(Integer.parseInt(Dashboard.myInvites)>0){
				txtCounter.setVisibility(View.VISIBLE);
				txtCounter.setText(Dashboard.myInvites);
			}else {
				txtCounter.setVisibility(View.GONE);
			}
			
			
		}else if (position==4) {
			txtTitle.setText(wew.get(position));
			if(Integer.parseInt(Dashboard.myNotifications)>0){
				txtCounter.setVisibility(View.VISIBLE);
				txtCounter.setText(Dashboard.myNotifications);
			}else {
				txtCounter.setVisibility(View.GONE);
			}
		}else{
			txtTitle.setText(wew.get(position));
			txtCounter.setVisibility(View.GONE);
		}
		
		
		//6, again num of built in menu, may change if more nav menus are added
		if (position<= 6){
			imgIcon.setImageResource(iconpic[position]);
		}else{
			txtTitle.setPadding(30, 0, 0, 0);
			imgIcon.setVisibility(View.GONE);
			txtTitle.setSingleLine();
			txtTitle.setEllipsize(android.text.TextUtils.TruncateAt.END);
			txtTitle.setTextSize(18);
			row.setBackgroundColor(Color.parseColor("#193a4d"));
			
		}
		
//		txtCounter.setText("9");
		return row;
	}
	
	
	
	
	
}

