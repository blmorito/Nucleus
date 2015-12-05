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
    $goal_id = $_POST["goal_id"];
    mysql_query("START TRANSACTION");

    $getProjectId = mysql_query("SELECT g.project_id,(SELECT user_id from projects_users where user_level_id = 1 and project_id = g.project_id) as project_leader, g.goal_name, g.user_id from goal g where g.goal_id = '$goal_id'");
    $r = mysql_fetch_array($getProjectId);

    $project_id = $r["project_id"];
    $goal_name = $r["goal_name"];
    $goal_creator = $r["user_id"];
    $project_leader = $r["project_leader"];

    $mj = mysql_query("INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id','$user_id','User','commented on a goal - ".$goal_name."',now())");

    $result = mysql_query("INSERT INTO comment (comment_type, comment_type_id, user_id, content, date_posted) VALUES ('Goal','$goal_id', '$user_id', '$content', now())");
   	

   	if ($result){
   		mysql_query("COMMIT");

      /*
       *NOTIFCATIONS
       */

      $notifyPL = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$project_leader', 'Goal', '$goal_id', 'commented on a goal - ".$goal_name."', now())");

      if($project_leader == $goal_creator){

      }else{
        $notifyGoalCreator = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$goal_creator', 'Goal', '$goal_id', 'commented on a goal - ".$goal_name."', now())");
      }

      $getCommenters = mysql_query("SELECT DISTINCT user_id FROM `comment` where comment_type = 'Goal' and comment_type_id = '$goal_id'");
      if(mysql_num_rows($getCommenters)>0){
          while($rowGet = mysql_fetch_array($getCommenters)){
            $toid = $rowGet["user_id"];


            if ($toid == $user_id || $toid == $goal_creator || $toid == $project_leader ){

            }else{
              $notifyCommentors = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$toid', 'Goal', '$goal_id', 'commented on a goal - ".$goal_name."', now())");
            }


          }

      }




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