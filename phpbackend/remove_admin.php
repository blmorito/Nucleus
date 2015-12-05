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
if (isset($_POST['workspace_id'])) {
   
    $workspace_id = $_POST["workspace_id"];
    $user_id = $_POST["profile_id"];

    mysql_query("START TRANSACTION");
    $result = mysql_query("UPDATE ws_members set user_level_id = '3' where user_id = '$user_id' and $workspace_id='$workspace_id'");
    if($result){
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

