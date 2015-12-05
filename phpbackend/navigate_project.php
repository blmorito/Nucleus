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

    $checkProject = mysql_query("SELECT * FROM projects where project_id = '$project_id'");
    if(mysql_num_rows($checkProject)>0){
      $r = mysql_fetch_array($checkProject);

      $getPlevel = mysql_query("SELECT user_level_id FROM projects_users where project_id = '$project_id' and user_id = '$user_id'");
      $row = mysql_fetch_array($getPlevel);

      $response["p_user_level_id"] = $row["user_level_id"];
      $response["success"] = 1;
      $response["project_name"] = $r["project_name"];
      echo json_encode($response);

    }else{
      $response["success"] = 0;
      echo json_encode($response);
    }
    
   
   
       
        
     

}
?>