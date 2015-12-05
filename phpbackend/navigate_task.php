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
    $project_id = $_POST["project_id"];
    $task_id = $_POST["task_id"];

    $checkProject = mysql_query("SELECT * FROM task where task_id = '$task_id'");
    if(mysql_num_rows($checkProject)>0){
      $r = mysql_fetch_array($checkProject);

      $getPlevel = mysql_query("SELECT user_level_id FROM projects_users where project_id = '$project_id' and user_id = '$user_id'");
      $row = mysql_fetch_array($getPlevel);

      $response["p_user_level_id"] = $row["user_level_id"];


      $getTask = mysql_query("SELECT t.task_id, t.task_name, t.date_due, t.task_status, t.date_finished, ax.user_id, tu.full_name, t.completed_by FROM `task` t LEFT JOIN (SELECT a.user_id, a.task_id from assignment a) ax ON ax.task_id = t.task_id LEFT JOIN (SELECT t.user_id, t.full_name from tbl_usersinfo t) tu ON tu.user_id = ax.user_id where t.task_id = '$task_id'") or trigger_error(mysql_error());
      $rr = mysql_fetch_array($getTask);

      $response["task_name"] = $rr["task_name"];
      $response["goal_creator_id"] = $rr["user_id"];
      $response["goal_creator"] = $rr["full_name"];

      //$phpdate = strtotime($rr["date_created"]);
            
            
      $phpdate = strtotime($rr["date_due"]);
      $phpdatex = date( 'M d, Y', $phpdate );

      
      $response["task_status"] = $rr["task_status"];

      if($rr["task_status"]=='Done'){
          $phpdate2 = strtotime($rr["date_finished"]);
          $phpdatex2 = date( 'M d, Y', $phpdate2 );
          $response["date_due"] = 'Completed on '.$phpdatex2.' by '.$rr['full_name'];
      }else{
          $response["date_due"] = 'Due on '.$phpdatex;
      }


      if(is_null($rr["full_name"])){
          $response["task_assignee_id"] = 0;
          $response["task_assignee"] = 'Anyone';
      }else{
          $response["task_assignee_id"] = $rr["user_id"];
          $response["task_assignee"] = $rr["full_name"];
      }

      $tempd = strtotime($rr["date_due"]);
        $deadline = strtotime(date('Y-m-d',$tempd));
        $now = strtotime(date('Y-m-d',time()));

        $response["date1"] = $deadline;
        $response["date2"] = $now;

        if($rr["task_status"]=='Done'){
            $response["isLate"] = "Not";
        }else{
            if($tempd<$now){
                $response["isLate"] = "Late";
            }else{
                $response["isLate"] = "Not";
            }
        }

      $qq = mysql_query("SELECT project_name from projects where project_id = '$project_id'");
      $rp = mysql_fetch_array($qq);

      $response["project_name"] = $rp["project_name"];
      $response["success"] = 1;
      echo json_encode($response);

    }else{
      $response["success"] = 0;
      echo json_encode($response);
    }
    
   
   
       
        
     

}
?>