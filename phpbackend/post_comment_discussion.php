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
if (isset($_POST['discussion_id'])) {
	$user_id = $_POST["user_id"];
	$content = $_POST["content"];
    $discussion_id = $_POST["discussion_id"];
    $getProjectId = mysql_query("SELECT project_id, d_subject from discussion where discussion_id = '$discussion_id'");
    $r = mysql_fetch_array($getProjectId);

    $project_id = $r["project_id"];
    $sub = $r["d_subject"];


    mysql_query("START TRANSACTION");
    $result = mysql_query("INSERT INTO comment (comment_type, comment_type_id, user_id, content, date_posted) VALUES ('Discussion','$discussion_id', '$user_id', '$content', now())");
    $query33 = "INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id','$user_id','User','commented on a discussion - ".$sub."',now())";
    $rrs = mysql_query($query33);
   	if ($result){
   		mysql_query("COMMIT");

      /*
       * NOTIFY 
       *
       */
      $getInfo = mysql_query("SELECT user_id, (SELECT user_id from discussion where discussion_id = '$discussion_id') as discussion_creator from projects_users where project_id = '$project_id' and user_level_id = '1'");
      $rowX = mysql_fetch_array($getInfo);

      $project_leader = $rowX["user_id"];
      $discussion_creator = $rowX["discussion_creator"];

      #notify project leader

      if($project_leader==$user_id){

      }else{
         
          $notifyPL = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$project_leader', 'Discussion', '$discussion_id','commented on the discussion - ".$sub."', now())");
      }

      #notify discussion creator
      if($discussion_creator==$user_id || $discussion_creator==$project_leader){

      }else{
         
          $notifyTL = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$discussion_creator', 'Discussion', '$discussion_id','commented on the discussion - ".$sub."', now())");
      }

     

      $getCommenters = mysql_query("SELECT DISTINCT user_id from comment where comment_type = 'Discussion' and comment_type_id = '$discussion_id'");
      if(mysql_num_rows($getCommenters)>0){
          while ($rowComment = mysql_fetch_array($getCommenters)) {
              $kid = $rowComment["user_id"];
              if ($kid == $user_id || $kid == $project_leader || $kid == $discussion_creator) {
                  # code...
              }else{
                  
                  $notifyCommCenter = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$kid', 'Discussion', '$discussion_id','commented on the discussion - ".$sub."', now())");
              }
              # code...
          }
      }

      /*
       * DONE 
       *
       */

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