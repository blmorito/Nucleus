<?php
 
/*
 * Following code will get single product details
 * A product is identified by product id (pid)
 */
 
// array for JSON response
$response = array();
$current_ip = '192.168.8.120';
 
// include db connect class
require_once __DIR__ . '/db_connect.php';
require '../PHPMailer/PHPMailerAutoload.php';
// connecting to db
$db = new DB_CONNECT();
 
// check for post data
if (isset($_POST['user_id'])) {


   
    $invite_id = $_POST['inviteId'];
    $user_id = $_POST['user_id'];
    $action = $_POST["action"];

    if($action == "Accept"){

        $result = mysql_query("SELECT * from ws_invites where invite_id = '$invite_id'");
        $row = mysql_fetch_array($result);  

        $workspace_id = $row["workspace_id"];
        $project_id = $row["project_id"];
        $email = $row["email"];
        mysql_query("START TRANSACTION");

        $isExist = mysql_query("SELECT * FROM ws_members where user_id = '$user_id' and workspace_id = '$workspace_id'");

        if (mysql_num_rows($isExist)>0){
            #do nothing
        }else{
            $queryInsertToWsMembers = mysql_query("INSERT INTO ws_members (workspace_id, user_id, user_level_id, date_joined) VALUES ('$workspace_id','$user_id','3',now() )");



        }

        
        if($project_id!=0){
            $queryInsertToProjMembers = mysql_query("INSERT INTO projects_users (project_id, user_id, date_joined, user_level_id) VALUES ('$project_id','$user_id',now(), '3')");

             ###########MAKING NOTIFICATIONS##################

            $getData = mysql_query("SELECT wi.invite_id, wi.user_id, wi.workspace_id, wi.project_id,(SELECT project_name from projects where project_id = wi.project_id) as projectname,(SELECT user_id from projects_users where project_id = wi.project_id and user_level_id = 1) as projectleader, (SELECT full_name from tbl_usersinfo where user_id = '$user_id') as invited FROM `ws_invites` wi WHERE invite_id = '$invite_id' and type = 'Project'") or trigger_error(mysql_error());
            $row_getdata = mysql_fetch_array($getData);

            $inviter_id = $row_getdata["user_id"];
            $project_leader = $row_getdata["projectleader"];
            $name_of_invited = $row_getdata["invited"];
            $project_name = $row_getdata["projectname"];

            $queryNotifyInviter = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$inviter_id', 'Project', '$project_id', 'has accepted your invitation to join the project ".$project_name."', now())") or trigger_error(mysql_error());

            if($inviter_id==$project_leader){
                #do not notify
            }else{
                $queryNotifyPL = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$project_leader', 'Project', '$project_id', 'has accepted the invitation to join the project ".$project_name."', now())") or trigger_error(mysql_error());
            }

            ################################END OF NOTIFICATION MAKER ##############
        }

        # making notification here
        $result2 = mysql_query("SELECT w.user_id as inviter,w.workspace_id,ws.workspace_name as wsname, w.project_id, p.project_name as pname, w.email as email, t.user_id, tu.full_name as fn from ws_invites w LEFT JOIN (SELECT tbl_users.user_id, tbl_users.email from tbl_users) t ON t.email = w.email LEFT JOIN (SELECT tbl_usersinfo.user_id, tbl_usersinfo.full_name from tbl_usersinfo)tu ON tu.user_id = t.user_id LEFT JOIN (SELECT workspaces.workspace_id, workspaces.workspace_name from workspaces) ws ON ws.workspace_id = w.workspace_id LEFT JOIN(SELECT projects.project_name, projects.project_id from projects)p ON p.project_id = w.project_id where w.workspace_id = '$workspace_id' and w.project_id = '$project_id' and w.email = '$email'");

        while($row = mysql_fetch_array($result2)){
            $temp = $row["inviter"];
            $wsname = $row["wsname"];
            $pname = $row["pname"];
            $msg="";
            if($project_id!=0){
                $msg = $row["fn"]." (".$email.") has accepted your invitation to the project ".$pname." in the workspace ".$wsname."";
                $msg = mysql_real_escape_string($msg);
            }else{
                $msg = $row["fn"]." (".$email.") has declined your invitation to the workspace ".$wsname.".";
                $msg = mysql_real_escape_string($msg);
            }
            
            $makeNotif = mysql_query("INSERT into notification (to_id, from_id, message, date_made) VALUES ('$temp', '$user_id','$msg', now())");
        }

        //delete of invite data
        $removeInviteRow = mysql_query("DELETE from ws_invites where workspace_id = '$workspace_id' and project_id = '$project_id' and email = '$email'");

        mysql_query("COMMIT");
        $response["success"] = 1;
        $response["workspace_id"] = $workspace_id;
        $response["message"] = "SUCCESS";
     
        // echoing JSON response
        echo json_encode($response);
    }elseif ($action == "Decline") {
        # code...

        $result = mysql_query("SELECT * from ws_invites where invite_id = '$invite_id'");
        $row = mysql_fetch_array($result);  

        $workspace_id = $row["workspace_id"];
        $project_id = $row["project_id"];
        $email = $row["email"];
        mysql_query("START TRANSACTION");

        if($project_id==0){
            $result2 = mysql_query("SELECT w.user_id as inviter,w.workspace_id,ws.workspace_name as wsname, w.project_id as pid, p.project_name as pname, w.email as email, t.user_id, tu.full_name as fn from ws_invites w LEFT JOIN (SELECT tbl_users.user_id, tbl_users.email from tbl_users) t ON t.email = w.email LEFT JOIN (SELECT tbl_usersinfo.user_id, tbl_usersinfo.full_name from tbl_usersinfo)tu ON tu.user_id = t.user_id LEFT JOIN (SELECT workspaces.workspace_id, workspaces.workspace_name from workspaces) ws ON ws.workspace_id = w.workspace_id LEFT JOIN(SELECT projects.project_name, projects.project_id from projects)p ON p.project_id = w.project_id where w.workspace_id = '$workspace_id' and w.email = '$email'");

            while($row = mysql_fetch_array($result2)){
                $temp = $row["inviter"];
                $wsname = $row["wsname"];
                $pname = $row["pname"];
                $pid = $row["pid"];
                $msg="";
                if($pid!=0){
                    $msg = $row["fn"]." (".$email.") has declined your invitation to the project ".$pname." in the workspace ".$wsname."";
                }else{
                    $msg = $row["fn"]." (".$email.") has declined your invitation to the workspace ".$wsname.".";
                    $msg = mysql_real_escape_string($msg);
                }
                
                $makeNotif = mysql_query("INSERT into notification (to_id, from_id, message, date_made) VALUES ('$temp', '$user_id','$msg', now())");
            }

            $deleteInv = mysql_query("DELETE from ws_invites where workspace_id = '$workspace_id' and email = '$email'");
            
            mysql_query("COMMIT");
            $response["success"] = 1;
            $response["message"] = "SUCCESS";
            
            // echoing JSON response
            echo json_encode($response);
        }else{
            $result2 = mysql_query("SELECT w.user_id as inviter,w.workspace_id,ws.workspace_name as wsname, w.project_id as pid, p.project_name as pname, w.email as email, t.user_id, tu.full_name as fn from ws_invites w LEFT JOIN (SELECT tbl_users.user_id, tbl_users.email from tbl_users) t ON t.email = w.email LEFT JOIN (SELECT tbl_usersinfo.user_id, tbl_usersinfo.full_name from tbl_usersinfo)tu ON tu.user_id = t.user_id LEFT JOIN (SELECT workspaces.workspace_id, workspaces.workspace_name from workspaces) ws ON ws.workspace_id = w.workspace_id LEFT JOIN(SELECT projects.project_name, projects.project_id from projects)p ON p.project_id = w.project_id where w.workspace_id = '$workspace_id' and w.project_id = '$project_id' and w.email = '$email'");

            while($row = mysql_fetch_array($result2)){
                $temp = $row["inviter"];
                $wsname = $row["wsname"];
                $pname = $row["pname"];
                $pid = $row["pid"];
                $msg="";
                
                $msg = $row["fn"]." (".$email.") has declined your invitation to the project ".$pname." in the workspace ".$wsname."";
                $msg = mysql_real_escape_string($msg);
                
                $makeNotif = mysql_query("INSERT into notification (to_id, from_id, message, date_made) VALUES ('$temp', '$user_id','$msg', now())");
            }

            $deleteInv = mysql_query("DELETE from ws_invites where workspace_id = '$workspace_id' and project_id = '$project_id' and email = '$email'");
            mysql_query("COMMIT");
            $response["success"] = 1;
            $response["message"] = "SUCCESSremove";
         
            // echoing JSON response
            echo json_encode($response);

        }
    }else{
        $response["success"] = 0;
        $response["message"] = "fail";
     
        // echoing JSON response
        echo json_encode($response);
    }

    

    

} else {
    // required field is missing
    $response["errorCode"] = 1;
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>