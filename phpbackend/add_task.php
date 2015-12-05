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
    $goal_id = $_POST["goal_id"];

    //getGoalname
    $getGoalz = mysql_query("SELECT goal_name from goal where goal_id = '$goal_id'" );
    $r = mysql_fetch_array($getGoalz);
    $gn = $r["goal_name"];

    $getProject = mysql_query("SELECT project_id from goal where goal_id = '$goal_id'");
    $wor = mysql_fetch_array($getProject);

    $project_id = $wor["project_id"];

    $task_name = mysql_real_escape_string($_POST["task_name"]);
    $assign_id = $_POST["assign_id"];
    $phpdate = strtotime($_POST['date_due']);
    $newphpdate = date('Y-m-d', $phpdate);

    mysql_query("START TRANSACTION");
    $del = mysql_query("DELETE FROM task where goal_id = '$goal_id' and task_name = 'This is a sample task made by nucleus'");

    
    $resultAA = mysql_query("SELECT status from goal where goal_id = '$goal_id'");
    $rowAA = mysql_fetch_array($resultAA);
    if($rowAA["status"]=='Done'){
        $wew = mysql_query("UPDATE goal set status = 'In progress', date_finished = '' where goal_id = '$goal_id'");
        $q = mysql_query("INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id', '$goal_id', 'Goal', 'has been re-opened', now())");
        $wew2 = mysql_query("UPDATE projects set project_status = 'In progress', date_finished = '' where project_id = '$project_id'");
    }    

    $result2 = mysql_query("INSERT INTO task (goal_id, user_id, task_name, date_created, date_due) VALUES ('$goal_id','$user_id','$task_name',now(), '$newphpdate')");


    $recentkey = mysql_insert_id();

    ##NOTIFIER FOR ADD TASK ##
    $getAllPMembers = mysql_query("SELECT user_id from projects_users where project_id = '$project_id' and user_id <> '$user_id'");

    while($rowGET = mysql_fetch_array($getAllPMembers)){
        $toid = $rowGET["user_id"];

        $notifyPMembers = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$toid', 'Task', '$recentkey' , 'added a task - ".$task_name."', now())") or trigger_error(mysql_error());
    }
    ## CODE ENDED #


    if($assign_id==0){

    }else{
        //get first name
        $iz = mysql_query("SELECT full_name from tbl_usersinfo where user_id = '$assign_id'");
        $r = mysql_fetch_array($iz);

        $fn = $r["full_name"];

        $resultz = mysql_query("INSERT INTO assignment (task_id, user_id) VALUES ('$recentkey', '$assign_id')");
        $u1 = $q = mysql_query("INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id', '$user_id', 'User', 'assigned the task ".$task_name." to ".$fn."', now())");

        ##NOTIFY ASSIGNEE###
            #notify assignee
            if($user_id==$assign_id){

            }else{
                $notifyAssign = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$assign_id', 'Task', '$recentkey' , 'assigned the task - ".$task_name." to you', now())") or trigger_error(mysql_error());
            }

            #notify project leader
            $getProL = mysql_query("SELECT user_id from projects_users where project_id = '$project_id' and user_level_id = '1'");
            $rz = mysql_fetch_array($getProL);
            $project_leader = $rz["user_id"];

            if($user_id==$project_leader){

            }else{
                $notifyAssign2 = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$assign_id', 'Task', '$recentkey' , 'assigned the task - ".$task_name." to ".$fn."', now())") or trigger_error(mysql_error());
            }

        ##DONEE##
    }
    if($result2){
        //activity
        $hmm = mysql_query("INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id', '$user_id', 'User', 'added a task - ".$task_name." in the goal ".$gn."', now())");

        mysql_query("COMMIT");
        $response["success"]=1;
        
        $response["message"]="Successfully added a task"."aaa".$gn;
        echo json_encode($response);
    }else{
        mysql_query("ROLLBACK");
        $response["success"]=0;
        $response["message"]="Something went wrong";
        echo json_encode($response);
    }

    

    
 

}
?>