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
   
   
    $ws_id = $_POST["workspace_id"];
    $email = $_POST["email"];

    $result = mysql_query("DELETE FROM ws_invites WHERE workspace_id = '$ws_id' and email = '$email'");
    if($result){

        $response["success"]=1;
        $response["message"] = "deletion success";
        echo json_encode($response);
    }else{
        $response["success"]=0;
        $response["message"] = "deletion failed";
        echo json_encode($response);
    }


 

}
?>