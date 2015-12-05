<?php
 date_default_timezone_set('Asia/Manila');
/*
 * Following code will get single product details
 * A product is identified by product id (pid)
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
// check for post data
if (isset($_POST['profile_id'])) {

	$profile_id = $_POST["profile_id"];
    $workspace_id = $_POST["workspace_id"];
    
    $response["log"] = $profile_id."   ".$workspace_id;
    $result = mysql_query("SELECT w.user_id, w.user_level_id,tu.full_name, tux.email, av.avatar_img  FROM `ws_members` w LEFT JOIN (SELECT t.user_id, t.full_name, t.avatar_id from tbl_usersinfo t) tu ON tu.user_id = w.user_id LEFT JOIN (SELECT x.user_id, x.email from tbl_users x) tux ON tux.user_id = tu.user_id LEFT JOIN (SELECT a.avatar_id, a.avatar_img from avatars a) av ON av.avatar_id = tu.avatar_id WHERE w.user_id = '$profile_id' and w.workspace_id = '$workspace_id'") or trigger_error(mysql_error());

    $row = mysql_fetch_array($result);
    $response["full_name"] = $row["full_name"];
    $response["user_level_id"] = $row["user_level_id"];
    $response["email"] = $row["email"];
    $response["avatar"] = base64_encode($row["avatar_img"]);

    //rorww
    $response["projects"] = array();
    $queryz = mysql_query("SELECT p.project_id, p.project_name, (SELECT COUNT(*) from task where goal_id IN (SELECT goal_id from goal where project_id = p.project_id)) AS TotalTasks, (SELECT COUNT(*) from task where goal_id IN (SELECT goal_id from goal where project_id = p.project_id) and task_status = 'Done') As TasksCompleted, (SELECT pu.user_id from projects_users pu  where project_id = p.project_id and user_level_id = 1)LeaderId, p.project_status  FROM `projects` p where project_id IN (SELECT project_id from projects_users where user_id = '$profile_id') and workspace_id = '$workspace_id' ORDER BY project_status ASC");

    while($rowx = mysql_fetch_array($queryz)){
        $project = array();
        $project["project_id"] = $rowx["project_id"];
        $project["project_name"] = $rowx["project_name"];
        $temp_lid = $rowx["LeaderId"];

        $wew = mysql_query("SELECT full_name from tbl_usersinfo where user_id = '$temp_lid'");
        $yow = mysql_fetch_array($wew);
        $project["project_leader"] = $yow["full_name"];
        $project["project_status"] = $rowx["project_status"];

        if($rowx["TotalTasks"] == 0){
            $project["progress"] = 0;
        }else{
            $total = $rowx["TotalTasks"];
            $com = $rowx["TasksCompleted"];

            $prog = round($com/$total * 100);
            $project["progress"] = $prog;
        }

        array_push($response["projects"], $project);


    }



    $response["success"] = 1;

    echo json_encode($response);
   
   
}else{
    $response["success"] = 0;
    $response["message"] = "Data error";
    echo json_encode($response);
}

    
 


?>