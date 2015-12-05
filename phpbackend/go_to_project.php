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
   
   
    $project_id = $_POST["project_id"];
    $user_id = $_POST["user_id"];

    $result = mysql_query("SELECT * FROM projects_users where user_id = '$user_id' and project_id = '$project_id'");
    $row = mysql_fetch_array($result);
    if(mysql_num_rows($result)>0){

        $response["p_user_level_id"] = $row["user_level_id"];

        $response["success"]=1;
        echo json_encode($response);
    }else{
        
        $response["success"]=0;
        $response["message"] = "You dont have access to this project";
        echo json_encode($response);
    }

    
 

}
?>