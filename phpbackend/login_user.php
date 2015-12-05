<?php
 
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
if (isset($_POST['email'])) {
    $email = $_POST['email'];
    $password = $_POST['password'];
    $encrypted = sha1($password);
 
    // get a product from products table
    $result = mysql_query("SELECT tbl_users.user_id, tbl_usersinfo.full_name, tbl_usersinfo.recent_ws, ws.user_level_id from tbl_users LEFT JOIN tbl_usersinfo ON tbl_users.user_id = tbl_usersinfo.user_id LEFT JOIN (SELECT w.user_id,w.workspace_id, w.user_level_id from ws_members w)ws ON ws.workspace_id = tbl_usersinfo.recent_ws and ws.user_id = tbl_users.user_id where tbl_users.email = '$email' AND tbl_users.password='$encrypted' ");
 
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
 
            $row = mysql_fetch_array($result);
            
            $response["user_id"] = $row["user_id"];
            $response["user_email"] = $email;
            $response["workspace_id"] = $row["recent_ws"];
            $response["ws_user_level"] = $row["user_level_id"];
    
            
            
            // success
            $response["success"] = 1;
 
           
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no product found
            $response["success"] = 0;
            $response["message"] = "No user found";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "No user found";
 
        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["errorCode"] = 1;
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>