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
    $goal_name = mysql_real_escape_string($_POST["goal_name"]);
    $goal_desc = mysql_real_escape_string($_POST["goal_desc"]);
    $goal_id = $_POST["goal_id"];

    $getGoalName = mysql_query("SELECT goal_name, project_id from goal where goal_id = '$goal_id'") or trigger_error(mysql_error());
    $r = mysql_fetch_array($getGoalName);

    $gn = $r["goal_name"];
    $project_id = $r["project_id"];

    if($gn == $goal_name){
      
    }else{

      /*
        *
        *NOTIFICATION MAKER
        */
            $queryNotifyPMembers = mysql_query("SELECT user_id FROM projects_users where project_id = '$project_id' and user_id <> '$user_id'") or trigger_error(mysql_error());
            while($rowPM = mysql_fetch_array($queryNotifyPMembers)){
                $response["loggerzzzzz"] = 'hi heheheh';
                $toid = $rowPM['user_id'];
                $queryNOTIFY = mysql_query("INSERT INTO notifications (project_id,from_id,to_id,type,type_id,content,date) VALUES ('$project_id', '$user_id', '$toid', 'Goal', '$goal_id', 'updated the goal - ".$goal_name."', now())") or trigger_error(mysql_error());
            }


        /*
        * Coded by Brylle..
        */

      $hmz = mysql_query("INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id', '$user_id','User', 'changed the goal name from ".$gn." to ".$goal_name."', now())") or trigger_error(mysql_error());
    }

    
    
    mysql_query("START TRANSACTION");
    $result = mysql_query("UPDATE goal set goal_name = '$goal_name', goal_desc = '$goal_desc' where goal_id = '$goal_id'") or trigger_error(mysql_error());
    

      if($result){
        mysql_query("COMMIT");
        $response["success"] = 1;
        echo json_encode($response);
      }else{
        mysql_query("ROLLBACK");
        $response["success"] = 0;
        $response["message"] = "Something went wrong";
        echo json_encode($response);
      }

}
?>