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

    $getGoalName = mysql_query("SELECT goal_name, project_id from goal where goal_id = '$goal_id'");
    $r = mysql_fetch_array($getGoalName);
    $goal_name = $r["goal_name"];
    $project_id = $r["project_id"];
    $q = mysql_query("INSERT INTO activity (project_id,from_id,from_type,message,date) VALUES ('$project_id', '$user_id', 'User', 'started the goal - ".$goal_name."',now())");

    mysql_query("START TRANSACTION");

    $result = mysql_query("UPDATE goal SET status = 'In progress', date_started = now() WHERE goal_id = '$goal_id'");

    if($result){
        mysql_query("COMMIT");
        $response["success"]= 1;
        $response["message"] = 'Successfully started the goal';
        echo json_encode($response);
    }else{
        mysql_query("ROLLBACK");
        $response["success"] =0;
        $response["message"] = 'Something went wrong';
        echo json_encode($response);
    }
    
    

    
 

}
?>