	package com.example.nucleus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.util.Log;
import android.widget.Toast;

public class userFunctions {
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	
	private JSONParser jsonParser;
	private static String registerURL = "http://"+ip+"/nucleus/api/add_user.php";
	private static String loginURL = "http://"+ip+"/nucleus/api/login_user.php";
	private static String createProjUrl = "http://"+ip+"/nucleus/api/createProject.php";
	private static String getwsname = "http://"+ip+"/nucleus/api/dashboard_getwsname.php";
	private static String forgotPassword = "http://"+ip+"/nucleus/api/forgotpw.php";
	private static String getMyInvites = "http://"+ip+"/nucleus/api/dashboard_getinvites.php";
	private static String getWsInfo = "http://"+ip+"/nucleus/api/db_wsinfo.php";
	private static String getWsMembers = "http://"+ip+"/nucleus/api/db_wsMembers.php";
	private static String getWsPendingMembers = "http://"+ip+"/nucleus/api/db_wsPendingMembers.php";
	private static String getWsMemberEmails = "http://"+ip+"/nucleus/api/ws_member_emails.php";
	private static String getWsMemberProjects = "http://"+ip+"/nucleus/api/ws_member_projects.php";
	private static String sendWorkspaceInvites = "http://"+ip+"/nucleus/api/send_invite_workspace.php";
	private static String cancelInviteWs = "http://"+ip+"/nucleus/api/cancel_invite_ws.php";
	private static String resendInviteWs = "http://"+ip+"/nucleus/api/resend_invite_ws.php";
	private static String respondInvite = "http://"+ip+"/nucleus/api/respondInvite.php";
	private static String getProfileData = "http://"+ip+"/nucleus/api/get_profile_data.php";
	private static String getAvatarForEdit = "http://"+ip+"/nucleus/api/get_avatar_for_edit.php";
	private static String uploadAvatar = "http://"+ip+"/nucleus/api/upload_avatar.php";
	private static String updateProfile = "http://"+ip+"/nucleus/api/update_profile.php";
	private static String updateCredentials = "http://"+ip+"/nucleus/api/update_credentials.php";
	private static String createWorkspace = "http://"+ip+"/nucleus/api/create_workspace.php";
	private static String getWorkspaces = "http://"+ip+"/nucleus/api/get_workspaces.php";
	private static String switchWorkspace = "http://"+ip+"/nucleus/api/switch_workspace.php";
	private static String getAllProjects = "http://"+ip+"/nucleus/api/get_all_projects.php";
	private static String getMyProjects = "http://"+ip+"/nucleus/api/get_my_projects.php";
	private static String goToProject = "http://"+ip+"/nucleus/api/go_to_project.php";
	private static String updateWorkspace = "http://"+ip+"/nucleus/api/update_workspace.php";
	private static String getProjectSetting = "http://"+ip+"/nucleus/api/get_project_setting.php";
	private static String updateProjectSetting = "http://"+ip+"/nucleus/api/update_project_setting.php";
	private static String getProjectInfo = "http://"+ip+"/nucleus/api/get_project_info.php";
	private static String getProjectMembers = "http://"+ip+"/nucleus/api/get_project_members.php";
	private static String getProjectMembersEmails = "http://"+ip+"/nucleus/api/get_project_members_emails.php";
	private static String sendProjectInvite = "http://"+ip+"/nucleus/api/send_project_invite.php";
	private static String getProjectPendingMembers = "http://"+ip+"/nucleus/api/get_project_pending_members.php";
	private static String resendProjectInvite = "http://"+ip+"/nucleus/api/resend_project_invite.php";
	private static String cancelProjectInvite = "http://"+ip+"/nucleus/api/cancel_project_invite.php";
	private static String postDiscussion = "http://"+ip+"/nucleus/api/post_discussion.php";
	private static String getDiscussions = "http://"+ip+"/nucleus/api/get_discussions.php";
	private static String deleteDiscussion = "http://"+ip+"/nucleus/api/delete_discussion.php";
	private static String updateDiscussion = "http://"+ip+"/nucleus/api/update_discussion.php";
	private static String viewDiscussion = "http://"+ip+"/nucleus/api/view_discussion.php";
	private static String postCommentDiscussion = "http://"+ip+"/nucleus/api/post_comment_discussion.php";
	private static String deleteComment = "http://"+ip+"/nucleus/api/delete_comment.php";
	private static String updateCommentDiscussion = "http://"+ip+"/nucleus/api/update_comment_discussion.php";
	private static String addGoal = "http://"+ip+"/nucleus/api/add_goal.php";
	private static String getProjectMembersForAssign = "http://"+ip+"/nucleus/api/get_pmembersinfo.php";
	private static String addTask = "http://"+ip+"/nucleus/api/add_task.php";
	private static String getOpenGoals = "http://"+ip+"/nucleus/api/get_open_goals.php";
	private static String getInProgressGoals = "http://"+ip+"/nucleus/api/get_inprogress_goals.php";
	private static String getDoneGoals = "http://"+ip+"/nucleus/api/get_done_goals.php";
	private static String startGoal = "http://"+ip+"/nucleus/api/start_goal.php";
	private static String getGoalInfoTasks = "http://"+ip+"/nucleus/api/get_goal_infotasks.php";
	private static String updateTask = "http://"+ip+"/nucleus/api/update_task.php";
	private static String viewGoalComments = "http://"+ip+"/nucleus/api/view_goal_comments.php";
	private static String postCommentGoal = "http://"+ip+"/nucleus/api/post_comment_goal.php";
	private static String deleteGoal = "http://"+ip+"/nucleus/api/delete_goal.php";
	private static String saveGoal = "http://"+ip+"/nucleus/api/update_goal.php";
	private static String getTaskInfo = "http://"+ip+"/nucleus/api/get_task_info.php";
	private static String saveTask = "http://"+ip+"/nucleus/api/save_task.php";
	private static String getWorkspaceMembers = "http://"+ip+"/nucleus/api/get_workspace_members.php";
	private static String getWsProfile = "http://"+ip+"/nucleus/api/get_ws_profile.php";
	private static String deleteTask = "http://"+ip+"/nucleus/api/delete_task.php";
	private static String makeAdmin = "http://"+ip+"/nucleus/api/make_admin.php";
	private static String removeAdmin = "http://"+ip+"/nucleus/api/remove_admin.php";
	private static String getWorkspaceActivities = "http://"+ip+"/nucleus/api/get_ws_activities.php";
	private static String getProjectStatus = "http://"+ip+"/nucleus/api/get_project_status.php";
	private static String startTask = "http://"+ip+"/nucleus/api/start_task.php";
	private static String getProjectMembersForAssignAndInfo = "http://"+ip+"/nucleus/api/get_pmembersinfo_and_info.php";
	private static String viewTaskComments = "http://"+ip+"/nucleus/api/view_task_comments.php";
	private static String postCommentTask = "http://"+ip+"/nucleus/api/post_comment_task.php";
	private static String getProjectActivities = "http://"+ip+"/nucleus/api/get_project_activities.php";
	private static String getUserActivities = "http://"+ip+"/nucleus/api/get_user_activities.php";
	private static String getNotifications = "http://"+ip+"/nucleus/api/get_notifications.php";
	private static String navigateProject = "http://"+ip+"/nucleus/api/navigate_project.php";
	private static String navigateGoal = "http://"+ip+"/nucleus/api/navigate_goal.php";
	private static String navigateTask = "http://"+ip+"/nucleus/api/navigate_task.php";
	private static String navigateDiscussion = "http://"+ip+"/nucleus/api/navigate_discussion.php";
	private static String navigateFile = "http://"+ip+"/nucleus/api/navigate_file.php";
	
	
	public userFunctions(){
        jsonParser = new JSONParser();
    }
	
	 public JSONObject registerUser(String fullName, String comp_org, String email, String password){
	        // Building Parameters
	        List params = new ArrayList();
	        params.add(new BasicNameValuePair("tag","register"));
	        params.add(new BasicNameValuePair("fullName", fullName));
	        
	        params.add(new BasicNameValuePair("comp_org", comp_org));
	       
	        params.add(new BasicNameValuePair("email", email));
	        params.add(new BasicNameValuePair("password", password));
	        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
	        return json;
	    }
	 
	 public JSONObject loginUser(String email, String password){
		 List params = new ArrayList();
		 params.add(new BasicNameValuePair("tag", "login"));
		 params.add(new BasicNameValuePair("email", email));
		 params.add(new BasicNameValuePair("password", password));
		 JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
		 return json;
		 
		 
		 
	 }
	 
	 public JSONObject createProject(String projectName, String projectDesc, ArrayList <String> invites, String workspace_id, String user_id, String due_date){
		 List params = new ArrayList();
		 params.add(new BasicNameValuePair("tag", "createproject"));
		 params.add(new BasicNameValuePair("projectName", projectName));
		 params.add(new BasicNameValuePair("projectDesc", projectDesc));
		 params.add(new BasicNameValuePair("workspace_id", workspace_id));
		 params.add(new BasicNameValuePair("user_id", user_id));
		 params.add(new BasicNameValuePair("due_date", due_date));
		 
		 if(invites.size()==0){
			 params.add(new BasicNameValuePair("invites[]", null));
		 }else{
			 for(int x=0; x<invites.size(); x++){
				 params.add(new BasicNameValuePair("invites[]", invites.get(x)));
			 }
		 }
		 
		 
		 //params.add(new BasicNameValuePair("invites[]", invites);
		 JSONObject json = jsonParser.getJSONFromUrl(createProjUrl, params);
		 return json;
		 
		 
		 
	 }
	 
	 //get workspace name for loading dashboard nav drawer
	 public JSONObject dashboardGetWSname(Integer workspace_id, String user_id){
		 List params = new ArrayList();
		 params.add(new BasicNameValuePair("workspace_id", Integer.toString(workspace_id)));
		 params.add(new BasicNameValuePair("user_id", user_id));
		 JSONObject json = jsonParser.getJSONFromUrl(getwsname, params);
		 return json;
	 }
	 
	 //password-reset
	 
	 public JSONObject forgotPassword(String email){
		 
		 List params = new ArrayList();
		 params.add(new BasicNameValuePair("email", email));
		 JSONObject json = jsonParser.getJSONFromUrl(forgotPassword, params);
		 return json;
	 }

	public JSONObject dashboardGetInvites(Integer user_id) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", Integer.toString(user_id)));
		
		JSONObject json = jsonParser.getJSONFromUrl(getMyInvites, params);
		return json;
	}

	public JSONObject getWSinfo(Integer ws_id, Integer user_id) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("workspace_id", ""+ws_id));
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		JSONParser jsonParser = new JSONParser();
		JSONObject json = new JSONObject();
		json = null;
		json = jsonParser.getJSONFromUrl(getWsInfo, params);
		
		return json;
	}

	public JSONObject getWsMembers(Integer ws_id) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("workspace_id", ""+ws_id));
		JSONParser jsonParser = new JSONParser();
		JSONObject json = new JSONObject();
		json = null;
		json = jsonParser.getJSONFromUrl(getWsMembers, params);
		
		return json;
	}
	
	public JSONObject getWsPendingMembers(Integer ws_id) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("workspace_id", ""+ws_id));
		JSONParser jsonParser = new JSONParser();
		JSONObject json = new JSONObject();
		json = null;
		json = jsonParser.getJSONFromUrl(getWsPendingMembers, params);
		
		return json;
	}

	public JSONObject getWsMemberEmails(Integer ws_id) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("workspace_id", ""+ws_id));
		JSONParser jsonParser = new JSONParser();
		JSONObject json = new JSONObject();
		json = null;
		json = jsonParser.getJSONFromUrl(getWsMemberEmails, params);
		
		return json;
	}

	public JSONObject getProjectsForInvite(Integer ws_id, Integer u_id) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("workspace_id", ""+ws_id));
		params.add(new BasicNameValuePair("user_id", ""+u_id));
		
		Log.e("params", ""+ws_id+" | "+u_id);
		JSONParser jsonParser = new JSONParser();
		JSONObject json = new JSONObject();
		json = null;
		json = jsonParser.getJSONFromUrl(getWsMemberProjects, params);
		
		return json;
	}

	public JSONObject sendWorkspaceInvites(Integer ws_id, Integer u_id, ArrayList<String> invites, ArrayList<String> selectedProjects) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("workspace_id", ""+ws_id));
		params.add(new BasicNameValuePair("user_id", ""+u_id));
		for(int x=0; x<invites.size(); x++){
			 params.add(new BasicNameValuePair("invites[]", invites.get(x)));
		 }
		if (selectedProjects.size()==0){
			params.add(new BasicNameValuePair("selectedProjects[]", null));
		}else{
			for(int x=0; x<selectedProjects.size(); x++){
				 params.add(new BasicNameValuePair("selectedProjects[]", selectedProjects.get(x)));
			 }
		}
		JSONParser jsonParser = new JSONParser();
		JSONObject json = new JSONObject();
		json = null;
		json = jsonParser.getJSONFromUrl(sendWorkspaceInvites, params);
		
		return json;
	}
	
	public JSONObject cancelWSinvitation(Integer ws_id, String email){
		List params = new ArrayList();
		params.add(new BasicNameValuePair("workspace_id", ""+ws_id));
		params.add(new BasicNameValuePair("email", email));
		
		JSONParser jsonParser = new JSONParser();
		JSONObject json = new JSONObject();
		json = null;
		json = jsonParser.getJSONFromUrl(cancelInviteWs, params);
		
		return json;
		
	}
	public JSONObject resendWSinvitation(Integer ws_id, String email){
		List params = new ArrayList();
		params.add(new BasicNameValuePair("workspace_id", ""+ws_id));
		params.add(new BasicNameValuePair("email", email));
		
		JSONParser jsonParser = new JSONParser();
		JSONObject json = new JSONObject();
		json = null;
		json = jsonParser.getJSONFromUrl(resendInviteWs, params);
		
		return json;
		
	}

	public JSONObject acceptOrDeclineInvite(Integer user_id, Integer inviteId,
			String inviteAction) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		params.add(new BasicNameValuePair("inviteId", ""+inviteId));
		params.add(new BasicNameValuePair("action", ""+inviteAction));
		
		JSONParser jsonParser = new JSONParser();
		JSONObject json = new JSONObject();
		json = null;
		
		Log.e("params", params.toString());
		json = jsonParser.getJSONFromUrl(respondInvite, params);
		
		return json;
		
		
	}

	public JSONObject getProfileData(Integer u_id) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", ""+u_id));
		JSONParser jsonParser = new JSONParser();
		JSONObject json = new JSONObject();
		json = null;
		
		Log.e("params", params.toString());
		json = jsonParser.getJSONFromUrl(getProfileData, params);
		
		return json;
	}

	public JSONObject getAvatarForEdit(Integer u_id) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", ""+u_id));
		JSONParser jsonParser = new JSONParser();
		JSONObject json = new JSONObject();
		json = null;
		
		Log.e("params", params.toString());
		json = jsonParser.getJSONFromUrl(getAvatarForEdit, params);
		
		return json;
	}

	public JSONObject uploadAvatar(String pathx, Integer user_id) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		params.add(new BasicNameValuePair("avatar", ""+pathx));
		JSONParser jsonParser = new JSONParser();
		JSONObject json = new JSONObject();
		json = null;
		
		Log.e("params", params.toString());
		json = jsonParser.getJSONFromUrl(uploadAvatar, params);
		
		return json;
	}

	public JSONObject updateProfile(String name, String address, Integer user_id) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		params.add(new BasicNameValuePair("full_name", name));
		params.add(new BasicNameValuePair("address", address));
		JSONParser jsonParser = new JSONParser();
		JSONObject json = new JSONObject();
		json = null;
		
		Log.e("params", params.toString());
		json = jsonParser.getJSONFromUrl(updateProfile, params);
		
		return json;
	}

	public JSONObject updateCredentials(Integer user_id, String pw,
			String newEmail, String newPassword) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		params.add(new BasicNameValuePair("password", pw));
		params.add(new BasicNameValuePair("new_email", newEmail));
		params.add(new BasicNameValuePair("new_password", newPassword));
		
		Log.e("params", params.toString());
		JSONParser jsonParser = new JSONParser();
		JSONObject json = new JSONObject();
		json = null;
		
		Log.e("params", params.toString());
		json = jsonParser.getJSONFromUrl(updateCredentials, params);
		
		return json;
	}

	public JSONObject createWorkspace(Integer user_id, String ws_name,
			String ws_desc, ArrayList<String> invites) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		params.add(new BasicNameValuePair("workspace_name", ws_name));
		params.add(new BasicNameValuePair("workspace_desc", ws_desc));
		 if(invites.size()==0){
			 params.add(new BasicNameValuePair("invites[]", null));
		 }else{
			 for(int x=0; x<invites.size(); x++){
				 params.add(new BasicNameValuePair("invites[]", invites.get(x)));
			 }
		 }
		
		
		Log.e("params", params.toString());
		Log.e("size", invites.size()+"");
		JSONParser jsonParser = new JSONParser();
		JSONObject json = new JSONObject();
		json = null;
		
		Log.e("params", params.toString());
		json = jsonParser.getJSONFromUrl(createWorkspace, params);
		
		return json;
	}

	public JSONObject getWorkspaces(Integer user_id) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", Integer.toString(user_id)));
		
		JSONObject json = jsonParser.getJSONFromUrl(getWorkspaces, params);
		return json;
	}

	public JSONObject switchWorkspace(Integer user_id, Integer toSwitchId) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", Integer.toString(user_id)));
		params.add(new BasicNameValuePair("workspace_id", Integer.toString(toSwitchId)));
		
		JSONObject json = jsonParser.getJSONFromUrl(switchWorkspace, params);
		return json;
	}

	public JSONObject getAllProjects(Integer workspace_id) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		
		params.add(new BasicNameValuePair("workspace_id", Integer.toString(workspace_id)));
		
		JSONObject json = jsonParser.getJSONFromUrl(getAllProjects, params);
		return json;
	}

	public JSONObject getMyProjects(Integer user_id, Integer workspace_id) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", Integer.toString(user_id)));
		params.add(new BasicNameValuePair("workspace_id", Integer.toString(workspace_id)));
		
		JSONObject json = jsonParser.getJSONFromUrl(getMyProjects, params);
		return json;
	}

	public JSONObject goToProject(Integer user_id, Integer goToProjectid) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", Integer.toString(user_id)));
		params.add(new BasicNameValuePair("project_id", Integer.toString(goToProjectid)));
		
		JSONObject json = jsonParser.getJSONFromUrl(goToProject, params);
		return json;
	}

	public JSONObject updateWorkspace(Integer workspace_id, String new_wsName,
			String new_wsDesc, Integer workspace_creator) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("workspace_id", Integer.toString(workspace_id)));
		params.add(new BasicNameValuePair("workspace_name", new_wsName));
		params.add(new BasicNameValuePair("workspace_desc", new_wsDesc));
		params.add(new BasicNameValuePair("workspace_creator", ""+workspace_creator));
		
		JSONObject json = jsonParser.getJSONFromUrl(updateWorkspace, params);
		return json;
	}

	public JSONObject getProjectSetting(Integer user_id, Integer project_id) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", Integer.toString(user_id)));
		params.add(new BasicNameValuePair("project_id", ""+project_id));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(getProjectSetting, params);
		return json;
	}

	public JSONObject updateProjectSetting(Integer project_id, String newName, String newDesc, Integer project_leader_id, String deadline) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("project_id", ""+project_id));
		params.add(new BasicNameValuePair("project_name", newName));
		params.add(new BasicNameValuePair("project_desc", newDesc));
		params.add(new BasicNameValuePair("project_leader_id", ""+project_leader_id));
		params.add(new BasicNameValuePair("deadline", ""+deadline));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(updateProjectSetting, params);
		return json;
	}

	public JSONObject getProjectInfo(Integer project_id) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		//params.add(new BasicNameValuePair("user_id", Integer.toString(user_id)));
		params.add(new BasicNameValuePair("project_id", ""+project_id));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(getProjectInfo, params);
		return json;
	}

	public JSONObject getProjectMembers(Integer project_id) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		//params.add(new BasicNameValuePair("user_id", Integer.toString(user_id)));
		params.add(new BasicNameValuePair("project_id", ""+project_id));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(getProjectMembers, params);
		return json;
	}

	public JSONObject getProjectMembersEmails(Integer project_id) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		//params.add(new BasicNameValuePair("user_id", Integer.toString(user_id)));
		params.add(new BasicNameValuePair("project_id", ""+project_id));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(getProjectMembersEmails, params);
		return json;
	}

	public JSONObject sendProjectInvite(Integer user_id, Integer workspace_id,
			Integer project_id, List<String> emailsToInvite) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", Integer.toString(user_id)));
		params.add(new BasicNameValuePair("workspace_id", Integer.toString(workspace_id)));
		params.add(new BasicNameValuePair("project_id", ""+project_id));
		for(int x=0; x<emailsToInvite.size(); x++){
			 params.add(new BasicNameValuePair("invites[]", emailsToInvite.get(x)));
		}
		
		JSONObject json = jsonParser.getJSONFromUrl(sendProjectInvite, params);
		return json;
	}

	public JSONObject getProjectPendingMembers(Integer project_id) {
		List params = new ArrayList();
		//params.add(new BasicNameValuePair("user_id", Integer.toString(user_id)));
		params.add(new BasicNameValuePair("project_id", ""+project_id));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(getProjectPendingMembers, params);
		return json;
		
		
		
	}

	public JSONObject resendProjectInvite(Integer user_id, Integer project_id, String resendEmail) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", Integer.toString(user_id)));
		params.add(new BasicNameValuePair("project_id", ""+project_id));
		params.add(new BasicNameValuePair("email", resendEmail));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(resendProjectInvite, params);
		return json;
	}

	public JSONObject cancelProjectInvite(Integer user_id, Integer project_id, String cancelEmail) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", Integer.toString(user_id)));
		params.add(new BasicNameValuePair("project_id", ""+project_id));
		params.add(new BasicNameValuePair("email", cancelEmail));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(cancelProjectInvite, params);
		return json;
	}

	public JSONObject postDiscussion(Integer user_id, Integer project_id,
			String subject, String body) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", Integer.toString(user_id)));
		params.add(new BasicNameValuePair("project_id", ""+project_id));
		params.add(new BasicNameValuePair("subject", subject));
		params.add(new BasicNameValuePair("body", body));
		
		
		
		
		JSONObject json = jsonParser.getJSONFromUrl(postDiscussion, params);
		return json;
	}

	public JSONObject getDiscussions(Integer project_id, String sortType) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		//params.add(new BasicNameValuePair("user_id", Integer.toString(user_id)));
		params.add(new BasicNameValuePair("project_id", ""+project_id));
		params.add(new BasicNameValuePair("sortType", ""+sortType));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(getDiscussions, params);
		return json;
	}

	public JSONObject deleteDiscussion(Integer toDeleteId) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		
		params.add(new BasicNameValuePair("discussion_id", ""+toDeleteId));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(deleteDiscussion, params);
		return json;
	}

	public JSONObject updateDiscussion(String discussion_id, String subject,
			String body, Integer user_id) {
		List params = new ArrayList();
		
		params.add(new BasicNameValuePair("discussion_id", discussion_id));
		params.add(new BasicNameValuePair("d_subject", subject));
		params.add(new BasicNameValuePair("d_body", body));
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(updateDiscussion, params);
		return json;
	}

	public JSONObject viewDiscussion(Integer discussion_id) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("discussion_id", ""+discussion_id));
		JSONObject json = jsonParser.getJSONFromUrl(viewDiscussion, params);
		return json;
	}

	public JSONObject postCommentDiscussion(Integer user_id,
			Integer discussion_id, String content) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", Integer.toString(user_id)));
		params.add(new BasicNameValuePair("discussion_id", ""+discussion_id));
		params.add(new BasicNameValuePair("content", content));
		JSONObject json = jsonParser.getJSONFromUrl(postCommentDiscussion, params);
		return json;
	}

	public JSONObject deleteComment(int toDeleteCommentId) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("comment_id", ""+toDeleteCommentId));
		
		JSONObject json = jsonParser.getJSONFromUrl(deleteComment, params);
		return json;
	}

	public JSONObject updateCommentDiscussion(int toUpdateCommentId,
			String toUpdateCommentContent) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("comment_id", ""+toUpdateCommentId));
		params.add(new BasicNameValuePair("comment_content", toUpdateCommentContent));
		
		JSONObject json = jsonParser.getJSONFromUrl(updateCommentDiscussion, params);
		return json;
	}

	public JSONObject addGoal(Integer user_id, Integer project_id,
			String goalName, String goalDesc) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		params.add(new BasicNameValuePair("project_id", ""+project_id));
		params.add(new BasicNameValuePair("goal_name", goalName));
		params.add(new BasicNameValuePair("goal_desc", goalDesc));
		
		JSONObject json = jsonParser.getJSONFromUrl(addGoal, params);
		return json;
	}

	public JSONObject getProjectMembersForAssign(Integer project_id) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("project_id", ""+project_id));
		
		JSONObject json = jsonParser.getJSONFromUrl(getProjectMembersForAssign, params);
		return json;
	}

	public JSONObject addTask(Integer user_id, int goal_id, String task_name,
			int assignId, String due_date) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		params.add(new BasicNameValuePair("goal_id", ""+goal_id));
		params.add(new BasicNameValuePair("task_name", task_name));
		params.add(new BasicNameValuePair("assign_id", ""+assignId));
		params.add(new BasicNameValuePair("date_due", ""+due_date));
		
		JSONObject json = jsonParser.getJSONFromUrl(addTask, params);
		return json;
	}

	public JSONObject getOpenGoals(Integer project_id) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("project_id", ""+project_id));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(getOpenGoals, params);
		return json;
	}

	public JSONObject getInProgressGoals(Integer project_id) {
		// TODO Auto-generated method stub
		List params = new ArrayList();
		params.add(new BasicNameValuePair("project_id", ""+project_id));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(getInProgressGoals, params);
		return json;
	}

	public JSONObject getDoneGoals(Integer project_id) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("project_id", ""+project_id));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(getDoneGoals, params);
		return json;
	}

	public JSONObject startGoal(Integer user_id, int toStartId) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		params.add(new BasicNameValuePair("goal_id", ""+toStartId));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(startGoal, params);
		return json;
	}

	public JSONObject getGoalInfoTasks(int goal_id) {
		List params = new ArrayList();
		
		params.add(new BasicNameValuePair("goal_id", ""+goal_id));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(getGoalInfoTasks, params);
		return json;
	}

	public JSONObject updateTask(int user_id, int goal_id, int taskToChange) {
		List params = new ArrayList();
		
		params.add(new BasicNameValuePair("goal_id", ""+goal_id));
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		params.add(new BasicNameValuePair("task_id", ""+taskToChange));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(updateTask, params);
		return json;
	}

	public JSONObject viewGoalComments(int goal_id) {
		List params = new ArrayList();
		
		params.add(new BasicNameValuePair("goal_id", ""+goal_id));
		
		JSONObject json = jsonParser.getJSONFromUrl(viewGoalComments, params);
		return json;
	}

	public JSONObject postCommentGoal(int user_id, int goal_id,
			String toCommentContent) {
		List params = new ArrayList();
		
		params.add(new BasicNameValuePair("goal_id", ""+goal_id));
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		params.add(new BasicNameValuePair("content", ""+toCommentContent));
		
		JSONObject json = jsonParser.getJSONFromUrl(postCommentGoal, params);
		return json;
	}

	public JSONObject deleteGoal(int user_id, int goal_id) {
		List params = new ArrayList();
		
		params.add(new BasicNameValuePair("goal_id", ""+goal_id));
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(deleteGoal, params);
		return json;
	}

	public JSONObject saveGoal(Integer user_id, Integer goal_id,
			String goalName, String goalDesc) {
		
		List params = new ArrayList();
		
		params.add(new BasicNameValuePair("goal_id", ""+goal_id));
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		params.add(new BasicNameValuePair("goal_name", ""+goalName));
		params.add(new BasicNameValuePair("goal_desc", ""+goalDesc));
		
		Log.e("paramsss", params.toString());
			
		JSONObject json = jsonParser.getJSONFromUrl(saveGoal, params);
		return json;
	}

	public JSONObject getTaskInfo(int task_id) {
		List params = new ArrayList();
		
		params.add(new BasicNameValuePair("task_id", ""+task_id));
		
		JSONObject json = jsonParser.getJSONFromUrl(getTaskInfo, params);
		return json;
	}

	public JSONObject saveTask(int user_id, int task_id, String newTaskName,
			Integer assignId, String due_date) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("task_id", ""+task_id));
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		params.add(new BasicNameValuePair("task_name", ""+newTaskName));
		params.add(new BasicNameValuePair("assignId", ""+assignId));
		params.add(new BasicNameValuePair("due_date", ""+due_date));
		Log.e("aaaa",""+assignId);
		JSONObject json = jsonParser.getJSONFromUrl(saveTask, params);
		return json;
	}

	public JSONObject getWorkspaceMembers(Integer workspace_id) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("workspace_id", ""+workspace_id));
		
		JSONObject json = jsonParser.getJSONFromUrl(getWorkspaceMembers, params);
		return json;
	}

	public JSONObject getWsProfileData(int profile_id, int workspace_id) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("profile_id", ""+profile_id));
		params.add(new BasicNameValuePair("workspace_id", ""+workspace_id));
		
		JSONObject json = jsonParser.getJSONFromUrl(getWsProfile, params);
		
		return json;
	}

	public JSONObject deleteTask(int user_id, int task_id) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		params.add(new BasicNameValuePair("task_id", ""+task_id));
		
		JSONObject json = jsonParser.getJSONFromUrl(deleteTask, params);
		
		return json;
	}

	public JSONObject makeAdmin(int profile_id, int workspace_id) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("profile_id", ""+profile_id));
		params.add(new BasicNameValuePair("workspace_id", ""+workspace_id));
		
		JSONObject json = jsonParser.getJSONFromUrl(makeAdmin, params);
		
		return json;
	}
	
	public JSONObject removeAdmin(int profile_id, int workspace_id) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("profile_id", ""+profile_id));
		params.add(new BasicNameValuePair("workspace_id", ""+workspace_id));
		
		JSONObject json = jsonParser.getJSONFromUrl(removeAdmin, params);
		
		return json;
	}

	public JSONObject getWorkspaceActivities(Integer workspace_id, String user_id) {
		List params = new ArrayList();
		
		params.add(new BasicNameValuePair("workspace_id", ""+workspace_id));
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		
		JSONObject json = jsonParser.getJSONFromUrl(getWorkspaceActivities, params);
		
		return json;
	}

	public JSONObject getProjectStatus(Integer project_id) {
		List params = new ArrayList();
		
		params.add(new BasicNameValuePair("project_id", ""+project_id));
		
		JSONObject json = jsonParser.getJSONFromUrl(getProjectStatus, params);
		
		return json;
	}

	public JSONObject startTask(int user_id, int goal_id, int task_id) {
		List params = new ArrayList();
		
		params.add(new BasicNameValuePair("goal_id", ""+goal_id));
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		params.add(new BasicNameValuePair("task_id", ""+task_id));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(startTask, params);
		return json;
	}

	public JSONObject getProjectMembersForAssignAndInfo(int project_id,
			int task_id) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("project_id", ""+project_id));
		params.add(new BasicNameValuePair("task_id", ""+task_id));
		
		JSONObject json = jsonParser.getJSONFromUrl(getProjectMembersForAssignAndInfo, params);
		return json;
	}

	public JSONObject viewTaskComments(int task_id) {
		List params = new ArrayList();
		
		params.add(new BasicNameValuePair("task_id", ""+task_id));
		
		JSONObject json = jsonParser.getJSONFromUrl(viewTaskComments, params);
		return json;
	}

	public JSONObject postCommentTask(int user_id, int task_id,
			String toCommentContent) {
		List params = new ArrayList();
		
		params.add(new BasicNameValuePair("task_id", ""+task_id));
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		params.add(new BasicNameValuePair("content", ""+toCommentContent));
		
		JSONObject json = jsonParser.getJSONFromUrl(postCommentTask, params);
		return json;
	}

	public JSONObject getProjectActivities(Integer project_id) {
		List params = new ArrayList();
		
		params.add(new BasicNameValuePair("project_id", ""+project_id));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(getProjectActivities, params);
		return json;
	}

	public JSONObject getUserActivities(Integer profile_id, Integer user_id, Integer workspace_id) {
		List params = new ArrayList();
		
		params.add(new BasicNameValuePair("profile_id", ""+profile_id));
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		params.add(new BasicNameValuePair("workspace_id", ""+workspace_id));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(getUserActivities, params);
		return json;
	}

	public JSONObject getNotifications(int user_id, int workspace_id) {
		List params = new ArrayList();
		
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		params.add(new BasicNameValuePair("workspace_id", ""+workspace_id));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(getNotifications, params);
		return json;
	}

	public JSONObject navigateProject(int user_id, int gotoPID) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		params.add(new BasicNameValuePair("project_id", ""+gotoPID));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(navigateProject, params);
		return json;
	}

	public JSONObject navigateGoal(int user_id, int gotoPID, int gotoGID) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		params.add(new BasicNameValuePair("project_id", ""+gotoPID));
		params.add(new BasicNameValuePair("goal_id", ""+gotoGID));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(navigateGoal, params);
		return json;
	}

	public JSONObject navigateTask(int user_id, int gotoPID, int gotoTID) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		params.add(new BasicNameValuePair("project_id", ""+gotoPID));
		params.add(new BasicNameValuePair("task_id", ""+gotoTID));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(navigateTask, params);
		return json;
	}

	public JSONObject navigateDiscussion(int user_id, int gotoPID, int gotoDID) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		params.add(new BasicNameValuePair("project_id", ""+gotoPID));
		params.add(new BasicNameValuePair("discussion_id", ""+gotoDID));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(navigateDiscussion, params);
		return json;
	}

	public JSONObject navigateFile(int user_id, int gotoPID, int gotoFID) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("user_id", ""+user_id));
		params.add(new BasicNameValuePair("project_id", ""+gotoPID));
		params.add(new BasicNameValuePair("file_id", ""+gotoFID));
		
		
		JSONObject json = jsonParser.getJSONFromUrl(navigateFile, params);
		return json;
	}



	

	
}
