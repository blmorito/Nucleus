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
if (isset($_POST['project_id'])) {
   
   
    $project_id = $_POST["project_id"];

    $query1 = mysql_query("SELECT  p.project_name, p.project_created, p.project_status  FROM `projects`p WHERE project_id = '$project_id'");
    $row1 = mysql_fetch_array($query1);

    $txt_projectName = $row1["project_name"];
    $response["txt_projectName"] = $txt_projectName;

    $txt_projectStatus = $row1["project_status"];
    $response["txt_projectStatus"] = $txt_projectStatus;

    $phpdate = strtotime($row1["project_created"]);
    $phpdatex = date( 'M d, Y', $phpdate );

    $txt_projectDate = $phpdatex;
    $response["txt_projectDate"] = $txt_projectDate;

    $query2 = mysql_query("SELECT p.user_id,MIN(user_level_id), tu.full_name from projects_users p LEFT JOIN (SELECT user_id,full_name from tbl_usersinfo) tu ON tu.user_id = p.user_id  where p.project_id = '$project_id'");
    $row2 = mysql_fetch_array($query2);

    $response["txt_projectCreator"] = $row2["full_name"];   

    $query3 = mysql_query("SELECT COUNT(*) as count, (SELECT COUNT(*) from goal where project_id = '$project_id') as goalcount, (SELECT COUNT(*) from goal where project_id = '$project_id' and status = 'Open') as goalopen, (SELECT COUNT(*) from goal where status = 'In progress' and project_id = '$project_id') as goalwip, (SELECT COUNT(*) from goal where project_id = '$project_id' and status = 'Done') as goaldone from projects_users where project_id = '$project_id'");
    $row3 = mysql_fetch_array($query3);

    $response["txt_totalMembers"] = $row3["count"];
    $response["txt_totalGoals"] = $row3["goalcount"];
    $response["txt_totalOpenGoals"] = $row3["goalopen"];
    $response["txt_totalWIPGoals"] = $row3["goalwip"];
    $response["txt_totalDoneGoals"] = $row3["goaldone"];

    $query4 = mysql_query("SELECT COUNT(*) as totaltask, (SELECT COUNT(*) from task where goal_id IN (SELECT goal_id from goal where project_id = '$project_id') and task_status = 'Done') AS taskcomplete, (SELECT COUNT(*) from task where task_status = 'Done' and (date_finished < date_due OR date_finished = date_due) and goal_id IN (SELECT goal_id from goal where project_id = '$project_id')) as taskontime, (SELECT COUNT(*) from task where task_status = 'Done' and date_finished>date_due and goal_id IN (SELECT goal_id from goal where project_id = '$project_id')) AS tasklate FROM task where goal_id IN (SELECT goal_id from goal where project_id = '$project_id')");
    $row4 = mysql_fetch_array($query4);

    $response["txt_totalNumTasks"] = $row4["totaltask"];
    $response["txt_totalCompleted"] = $row4["taskcomplete"];
    $response["txt_totalCompletedOnTime"] = $row4["taskontime"];
    $response["txt_totalCompletedLate"] = $row4["tasklate"];


    if ($row4["totaltask"]==0){
        $prog = 0;
    }else{
        $prog = round(($row4["taskcomplete"]/$row4["totaltask"])*100);
    }
    $response["progress"] = $prog;

    if($response["txt_projectStatus"] == 'In progress'){
        $response["txt_projectStatus"] = 'In progress - '.$prog.'%';
    }

    $response["success"] = 1;
    echo json_encode($response);



    
 

}
?>