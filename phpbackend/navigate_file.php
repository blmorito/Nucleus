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
    $file_id = $_POST["file_id"];

    
    
       
        
     

}
?>