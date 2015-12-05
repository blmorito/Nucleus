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
   
   
    $task_id = $_POST["task_id"];
    $user_id = $_POST["user_id"];

    $getGoalAndCount = mysql_query("SELECT t.task_id, t.goal_id, (SELECT project_id from goal where goal_id = t.goal_id)project_id, (SELECT COUNT(*) FROM task where goal_id = t.goal_id GROUP BY goal_id) gcount FROM `task` t where task_id = '$task_id'");
    $row = mysql_fetch_array($getGoalAndCount);
    $gcount = $row["gcount"];
    $goal_id = $row["goal_id"];
    $project_id = $row["project_id"];

    $gettname = mysql_query("SELECT task_name from task where task_id = '$task_id'");
    $r = mysql_fetch_array($gettname);
    $task_name = $r["task_name"];

    $gCounter = mysql_query("SELECT COUNT(*) FROM task where goal_id = '$goal_id'");
    $jkj = mysql_fetch_array($gCounter);

    $kk = $jkj[0];
    $response["flaggg"] = $kk."   ".$gcount;
    $deleteTask = mysql_query("DELETE from task where task_id = '$task_id'");
    $resultX = mysql_query("DELETE FROM assignment where task_id = '$task_id'");

    $q = mysql_query("INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id','$user_id','User','deleted the task - ".$task_name."',now())");

    if($kk==1){
        $result = mysql_query("DELETE FROM goal where goal_id = '$goal_id'");
        $result1 = mysql_query("DELETE FROM task where goal_id = '$goal_id'");
        $result2 = mysql_query("DELETE FROM comment where comment_type = 'Goal' and comment_type_id = '$goal_id'");
        $result3 = mysql_query("DELETE FROM assignment where task_id IN (Select task_id from task where goal_id = '$goal_id')");

        $queryGOALS = mysql_query("SELECT p.project_id, p.project_name, p.project_status, (SELECT COUNT(*) FROM goal where project_id = '$project_id') as TotalGoals, (SELECT COUNT(*) FROM goal where project_id = '$project_id' and status = 'Done') as DoneGoals FROM `projects` p where project_id = '$project_id'");

          $row1 = mysql_fetch_array($queryGOALS);


          $totalG = $row1["TotalGoals"];
          $completeG = $row1["DoneGoals"];

          $response["tg"] = $totalG;
          if($totalG !=0){
              if($totalG==$completeG){
                  $queryProCom = mysql_query("UPDATE projects set project_status = 'Done', date_finished = now() where project_id = '$project_id'");
              }
          }else{
            $queryProCom = mysql_query("UPDATE projects set project_status = 'In progress', date_finished='' where project_id = '$project_id'");
          }
    }

    $response["success"] = 1;
    $response["message"] = "deleted task";
    echo json_encode($response);


    
    
    

    
 

}
?>