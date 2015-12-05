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
    
    $query = mysql_query("SELECT * FROM workspaces where workspace_id = '$workspace_id'");
    if(mysql_num_rows($query)>0){

        $row = mysql_fetch_array($query);
        $response["workspace_name"]

    }else{
        $response["success"] = 0;
        $response["message"] = "Something went wrong. Cannot locate workspace information";

        echo json_encode($response);
    }

    
 

}
?>

