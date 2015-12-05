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
if (isset($_POST['comment_id'])) {
   
   
    $comment_id = $_POST["comment_id"];

    mysql_query("START TRANSACTION");
    $result = mysql_query("DELETE from comment where comment_id = '$comment_id'");
    if($result){
        mysql_query("COMMIT");
        $response["success"] = 1;
        $response["message"] = "Successfully deleted the comment";
        echo json_encode($response);
    }else{
        mysql_query("ROLLBACK");
        $response["success"] = 0;
        $response["message"] = "Something went wrong";
        echo json_encode($response);
    }

    

    
 

}
?>