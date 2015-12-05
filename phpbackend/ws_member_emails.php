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

    $result = mysql_query("SELECT m.workspace_id, m.user_id, u.email as email from ws_members m LEFT JOIN (SELECT tbl_users.user_id, tbl_users.email from tbl_users) u on m.user_id = u.user_id where m.workspace_id = '$ws_id'") or trigger_error(mysql_error());

    if(mysql_num_rows($result)>0){

        $response["members"] = array();
    	
        while($row = mysql_fetch_array($result)){
       		
       		$member = array();
           
            $member["email"] = $row["email"];
            
			
			array_push($response["members"], $member);

            

        }
        $response["success"]=1;
        echo json_encode($response);
    }else{
        $response["success"] = 0;
        $response["message"] = "Error CYA";
        echo json_encode($response);
    }

    
 

}
?>