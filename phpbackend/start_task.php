<?php
 date_default_timezone_set('Asia/Manila');

 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
// check for post data
if (isset($_POST['user_id'])) {
   
   
    $goal_id = $_POST["goal_id"];
    $user_id = $_POST["user_id"];
    $task_id = $_POST["task_id"];

    $query1 = mysql_query("SELECT project_id,status,goal_name, (SELECT task_name from task where task_id = '$task_id') AS task_name from goal where goal_id = '$goal_id'");
    $row1 = mysql_fetch_array($query1);
    $project_id = $row1["project_id"];
    $task_name = $row1["task_name"];
    $goal_status = $row1["status"];
    $goal_name = $row1["goal_name"];

    mysql_query("START TRANSACTION");
    $query2 = mysql_query("INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id','$user_id','User','started the task - ".$task_name."',now())") or trigger_error(mysql_error());
    $query3 = mysql_query("UPDATE task set task_status = 'In progress', date_started = now() where task_id = '$task_id'") or trigger_error(mysql_error());

    if ($goal_status==='Open'){
        $query4 = mysql_query("UPDATE goal set status = 'In progress' where goal_id = '$goal_id'") or trigger_error(mysql_error());
        $query5 = mysql_query("INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id','$user_id','User','started the goal - ".$goal_name."',now())") or trigger_error(mysql_error());
    }

    if($query2 && $query3){
        mysql_query("COMMIT");

        $getProjectleader = mysql_query("SELECT user_id, (SELECT user_id from task where task_id = '$task_id') AS task_creator from projects_users where project_id = '$project_id' and user_level_id = '1'");
        $rowGetPL = mysql_fetch_array($getProjectleader);
        $project_leader = $rowGetPL["user_id"];
        $task_creator = $rowGetPL["task_creator"];



        #NOTIFY#
        /*
         * NOTIFY 
         *
         */

        #notify project leader

        if($project_leader==$user_id){

        }else{
            $notifyPLstart = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$project_leader', 'Task', '$task_id','started the task - ".$task_name."', now())");
            
        }

        #notify task creator
        if($task_creator==$user_id || $task_creator==$project_leader){

        }else{
            $notifyTLstart = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$task_creator', 'Task', '$task_id','started the task - ".$task_name."', now())");
            
        }

        #notify assignee
        $getAssignee = mysql_query("SELECT user_id from assignment where task_id = '$task_id'");
        $assign_id = 0;
        if(mysql_num_rows($getAssignee)>0){

            $rowAsgn = mysql_fetch_array($getAssignee);
            $assign_id = $rowAsgn["user_id"];

            if($assign_id==$user_id || $assign_id==$project_leader || $assign_id==$task_creator){

            }else{
                $notifyAsgnStart = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$assign_id', 'Task', '$task_id','started the task - ".$task_name."', now())");
                
            }
        }

        $getCommenters = mysql_query("SELECT DISTINCT user_id from comment where comment_type = 'Task' and comment_type_id = '$task_id'");
        if(mysql_num_rows($getCommenters)>0){
            while ($rowComment = mysql_fetch_array($getCommenters)) {
                $kid = $rowComment["user_id"];
                if ($kid == $user_id || $kid == $project_leader || $kid == $task_creator) {
                    # code...
                }else{
                    $notifyCommCenterStart = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$kid', 'Task', '$task_id','started the task - ".$task_name."', now())");
                    
                }
                # code...
            }
        }

        /*
         * DONE 
         *
         */
        #END

        $response["trap"] = $goal_status;
        $response["success"] = 1;
        $response["message"] = 'Successfully started the task';
        echo json_encode($response);
    }else{
        mysql_query("ROLLBACK");
        $response["success"] = 0;
        $response["message"] = 'Something went wrong';
        echo json_encode($response);
    }


    

   
    
 

}
?>