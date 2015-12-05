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
    $getwsid = mysql_query("SELECT workspace_id from projects where project_id = '$project_id'");
    $roww = mysql_fetch_array($getwsid);
    $workspace_id = $roww["workspace_id"];

    $getWSmembers = mysql_query("SELECT email from tbl_users where email NOT IN (SELECT email FROM projects_users p LEFT JOIN tbl_users tu ON tu.user_id = p.user_id WHERE project_id = '$project_id') and user_id IN (SELECT user_id from ws_members where workspace_id='$workspace_id')") or trigger_error(mysql_error());

        $response["wsemails"] = array();
        while($rowu = mysql_fetch_array($getWSmembers)){
            $member = array();
            $member["email"] = $rowu["email"];

            array_push($response["wsemails"], $member);
        }

    $result = mysql_query("SELECT email FROM projects_users p LEFT JOIN tbl_users tu ON tu.user_id = p.user_id WHERE project_id = '$project_id'") or trigger_error(mysql_error());

    if(mysql_num_rows($result)>0){

        $response["emails"] = array();
    	
        while($row = mysql_fetch_array($result)){
       		
       		$member = array();
            $member["email"] = $row["email"];
			
			array_push($response["emails"], $member);

            

        }
        $response["success"]=1;
        echo json_encode($response);
    }else{
        $response["success"] = 0;
        $response["message"] = "Error CYA";
        $response["errorCode"] = 1;
        echo json_encode($response);
    }

    
 

}
?>