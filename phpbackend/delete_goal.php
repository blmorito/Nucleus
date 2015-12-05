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
    $getProject = mysql_query("SELECT project_id from goal where goal_id = '$goal_id'");
    $wor = mysql_fetch_array($getProject);

    $project_id = $wor["project_id"];

    $getGoalName = mysql_query("SELECT goal_name from goal where goal_id = '$goal_id'");
    $r = mysql_fetch_array($getGoalName);
    $goal_name = $r["goal_name"];
    
    mysql_query("START TRANSACTION");
    $result = mysql_query("DELETE FROM goal where goal_id = '$goal_id'");
    $result1 = mysql_query("DELETE FROM task where goal_id = '$goal_id'");
    $result2 = mysql_query("DELETE FROM comment where comment_type = 'Goal' and comment_type_id = '$goal_id'");
    $result3 = mysql_query("DELETE FROM assignment where task_id IN (Select task_id from task where goal_id = '$goal_id')");

    $act = mysql_query("INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id', '$user_id', 'User', 'deleted a goal - ".$goal_name."',now())");

     $queryGOALS = mysql_query("SELECT p.project_id, p.project_name, p.project_status, (SELECT COUNT(*) FROM goal where project_id = '$project_id') as TotalGoals, (SELECT COUNT(*) FROM goal where project_id = '$project_id' and status = 'Done') as DoneGoals FROM `projects` p where project_id = '$project_id'");

      $row1 = mysql_fetch_array($queryGOALS);


      $totalG = $row1["TotalGoals"];
      $completeG = $row1["DoneGoals"];

      if($totalG !=0){
          if($totalG==$completeG){
              $queryProCom = mysql_query("UPDATE projects set project_status = 'Done', date_finished = now() where project_id = '$project_id'");
          }
      }else{
        $queryProCom = mysql_query("UPDATE projects set project_status = 'In progress', date_finished='' where project_id = '$project_id'");
      }

      if($result && $result1 && $result2 && $result3){
        mysql_query("COMMIT");
        $response["success"] = 1;
        echo json_encode($response);
      }else{
        mysql_query("ROLLBACK");
        $response["success"] = 0;
        echo json_encode($response);
      }

   
   
       
        
     

}
?>