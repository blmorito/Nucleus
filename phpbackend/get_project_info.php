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
if (isset($_POST['project_id'])) {
   
   
    $project_id = $_POST["project_id"];

    $result = mysql_query("SELECT * FROM projects where project_id = '$project_id'");

    $row = mysql_fetch_array($result);

    if(mysql_num_rows($result)>0){
        $response["success"] = 1;
        $response["project_name"] = $row["project_name"];
        $response["project_desc"] = $row["project_desc"];

        echo json_encode($response);
    }else{
        $response["success"] = 0;
        $response["message"] = "Something went horribly wrong";
        echo json_encode($response);
    }

    
 

}
?>