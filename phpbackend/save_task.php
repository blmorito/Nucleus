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
if (isset($_POST['user_id'])) {
   
   
    
    $user_id = $_POST["user_id"];
    $task_id = $_POST["task_id"];
    $task_name = $_POST["task_name"];
    $assign_id = $_POST["assignId"];
    $due_date = $_POST["due_date"];
    $getGoalId = mysql_query("SELECT goal_id from task where task_id = '$task_id'");
    $r = mysql_fetch_array($getGoalId);
    $goal_id = $r["goal_id"];
    $getProjectId = mysql_query("SELECT project_id from goal where goal_id = '$goal_id'");
    $rr = mysql_fetch_array($getProjectId);
    $project_id = $rr["project_id"];

    $getTaskName = mysql_query("SELECT task_name from task where task_id = '$task_id'");
    $rr = mysql_fetch_array($getTaskName);
    $task_name2 = $rr["task_name"];

    $getname = mysql_query("SELECT full_name from tbl_usersinfo where user_id = '$assign_id'") or trigger_error(mysql_error());
    $rrr = mysql_fetch_array($getname);
    $fn = $rrr["full_name"];

    $phpdate = strtotime($due_date);
    $phpdatex = date('Y-m-d', $phpdate);
    mysql_query("START TRANSACTION");
    if($assign_id==0){
        $removeAssign = mysql_query("DELETE FROM assignment where task_id = '$task_id'");
    }else{
        $removeAssign = mysql_query("DELETE FROM assignment where task_id = '$task_id'");
        $updateAssign = mysql_query("INSERT INTO assignment (user_id, task_id) VALUES ('$assign_id','$task_id')");

        $q = mysql_query("INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id', '$user_id', 'User', 'assigned the task ".$task_name." to ".$fn."', now())");

        ##NOTIFY ASSIGNEE###
            #notify assignee
            if($user_id==$assign_id){

            }else{
                $notifyAssign = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$assign_id', 'Task', '$task_id' , 'assigned the task - ".$task_name." to you', now())") or trigger_error(mysql_error());
            }

            #notify project leader
            $getProL = mysql_query("SELECT user_id from projects_users where project_id = '$project_id' and user_level_id = '1'");
            $rz = mysql_fetch_array($getProL);
            $project_leader = $rz["user_id"];

            if($assign_id==$project_leader || $assign_id==$user_id){

            }else{
                $notifyAssign2 = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$assign_id', 'Task', '$task_id' , 'assigned the task - ".$task_name." to ".$fn."', now())") or trigger_error(mysql_error());
            }

        ##DONEE##
    }

    $updateTask = mysql_query("UPDATE task set task_name = '$task_name', date_due ='$phpdatex' where task_id = '$task_id'");
    if($task_name == $task_name2){

    }else{
        $qwx = mysql_query("INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id', '$user_id', 'User', 'changed the task from ".$task_name2." to ".$task_name."', now())");
    }
    
    if($updateTask){
        mysql_query("COMMIT");
        /*
         * NOTIFY
         *
         */

        $queryNotifyPMembers = mysql_query("SELECT user_id FROM projects_users where project_id = '$project_id' and user_id <> '$user_id'") or trigger_error(mysql_error());
        while($rowPM = mysql_fetch_array($queryNotifyPMembers)){

            $toid = $rowPM['user_id'];
            $queryNOTIFY = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$toid', 'Task', '$task_id', 'updated a task - ".$task_name."', now())") or trigger_error(mysql_error());
        }

        #END

        $response["success"] = 1;
        echo json_encode($response);
    }else{
        mysql_query("ROLLBACK");
        $response["success"] = 0;
        echo json_encode($response);
    }

    

    
 

}
?>