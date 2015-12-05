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

    $result = mysql_query("SELECT m.workspace_id as workspace_id, w.workspace_name as workspace_name, w.workspace_desc as workspace_desc FROM `ws_members`m LEFT JOIN (SELECT workspaces.workspace_id, workspaces.workspace_name, workspaces.workspace_desc from workspaces) w ON w.workspace_id = m.workspace_id WHERE user_id = '$user_id'");

    if(mysql_num_rows($result)>0){

        $response["workspaces"] = array();

        while($row = mysql_fetch_array($result)){
            

            $workspace = array();
            $workspace["workspace_id"] = $row["workspace_id"];
            $workspace["workspace_name"] = $row["workspace_name"];
            $workspace["workspace_desc"] = $row["workspace_desc"];
            
            array_push($response["workspaces"], $workspace);

            

        }
        $response["success"]=1;
        echo json_encode($response);
    }else{
        $response["success"] = 0;
        $response["message"] = "No workspaces found for this user";
        echo json_encode($response);
    }

    
 

}
?>