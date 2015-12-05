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
    $workspace_id = $_POST["workspace_id"];

    $result = mysql_query("UPDATE tbl_usersinfo set recent_ws = '$workspace_id' where user_id = '$user_id'");

    if ($result){
         $response["success"]=1;

         $getLevel = mysql_query("SELECT user_level_id from ws_members where user_id = '$user_id' and workspace_id = '$workspace_id'");
         $row = mysql_fetch_array($getLevel);
         $response["workspace_id"] = $workspace_id;
         $response["ws_user_level_id"] = $row["user_level_id"];
         echo json_encode($response);
    
       
    }else{
        $response["success"] = 0;
        $response["message"] = "Something went wrong";
        echo json_encode($response);
    }

    
 

    }
?>