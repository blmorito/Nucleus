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
  	$content = $_POST["content"];
    $task_id = $_POST["task_id"];
    mysql_query("START TRANSACTION");

    $getProjectId = mysql_query("SELECT (SELECT user_id from task where task_id = '$task_id') AS task_creator, (SELECT task_name from task where task_id = '$task_id') AS task_namez, project_id, goal_name from goal  where goal_id = (SELECT goal_id from task where task_id = '$task_id' )");
    $r = mysql_fetch_array($getProjectId);

    $project_id = $r["project_id"];
    $goal_name = $r["goal_name"];
    $task_name = $r["task_namez"];
    $task_creator = $r["task_creator"];

    $mj = mysql_query("INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id','$user_id','User','commented on a task - ".$task_name."',now())");

    $result = mysql_query("INSERT INTO comment (comment_type, comment_type_id, user_id, content, date_posted) VALUES ('Task','$task_id', '$user_id', '$content', now())");
   	


    ##NOTIFY ##
    #notify task creator
    if($task_creator==$user_id){
      #do nothing
    }else{
      $notifyTaskCreator = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$task_creator', 'Task', '$task_id', 'commented on the task - ".$task_name."', now())");
    }

    #notify project_leader
    $getPL = mysql_query("SELECT user_id from projects_users where project_id = '$project_id' and user_level_id='1'");
    $rowPL = mysql_fetch_array($getPL);

    $project_leader = $rowPL["user_id"];
    if($project_leader==$user_id || $project_leader==$task_creator){

    }else{
      $notifyPL = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$project_leader', 'Task', '$task_id', 'commented on the task - ".$task_name."', now())");
    }

    #notify commenters
    $getAllCommenters = mysql_query("SELECT DISTINCT user_id FROM comment where comment_type = 'Task' and comment_type_id = '$task_id'");

    if(mysql_num_rows($getAllCommenters)>0){
      while ($rowAll = mysql_fetch_array($getAllCommenters)) {

        $toid = $rowAll["user_id"];
        if($toid==$user_id || $toid==$task_creator || $toid==$project_leader){

        }else{
          $notifyCommenters = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$toid', 'Task', '$task_id', 'commented on the task - ".$task_name."', now())");
        }

      }
    }

    #notify assignee
    $getAssignee = mysql_query("SELECT user_id from assignment where task_id = '$task_id'");

    $a_id = 0;
    if (mysql_num_rows($getAssignee)>0) {
      $rowAsgn = mysql_fetch_array($getAssignee);
      $a_id = $rowAsgn["user_id"];

      if($a_id==$user_id || $a_id==$task_creator || $a_id==$project_leader){

      }else{
        $notifyCommenters = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$a_id', 'Task', '$task_id', 'commented on the task - ".$task_name."', now())");
      }
    }

    #notify completor & starter
    $getCompletor = mysql_query("SELECT completed_by, started_by from task where task_id = '$task_id'");
    $rowC = mysql_fetch_array($getCompletor);

    $completed_by = $rowC["completed_by"];
    $started_by = $rowC["started_by"];

    $checkIfCommenterC = mysql_query("SELECT user_id from comment WHERE comment_type = 'Task' and comment_type_id = '$task_id' and user_id = '$completed_by'");
    if(mysql_num_rows($checkIfCommenterC)>0){

    }else{
      if($completed_by==$user_id || $completed_by==$task_creator || $completed_by==$project_leader){

      }else{
        $notifyCompleter = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$completed_by', 'Task', '$task_id', 'commented on the task - ".$task_name."', now())");
      }
    }

    $checkIfCommenterS = mysql_query("SELECT user_id from comment WHERE comment_type = 'Task' and comment_type_id = '$task_id' and user_id = '$started_by'");
    if(mysql_num_rows($checkIfCommenterS)>0){

    }else{
      if($started_by==$user_id || $started_by==$task_creator || $started_by==$project_leader || $started_by == $completed_by){

      }else{
        $notifyStarter = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$started_by', 'Task', '$task_id', 'commented on the task - ".$task_name."', now())");
      }
    }



    ##############DONE NOTIFYINGGGGGGGGGGGGG######


   	if ($result){
   		mysql_query("COMMIT");

   		$response["success"] = 1;
   		echo json_encode($response);
   	}else{
   		mysql_query("ROLLBACK");
   		$response["success"]=0;
   		$response["message"]="Something went wrong";
   		echo json_encode($response);
   	}
   
   
       
        
     

}
?>