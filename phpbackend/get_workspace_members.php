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

    $result = mysql_query("SELECT w.user_id, tu.full_name  FROM `ws_members` w LEFT JOIN (SELECT u.user_id, u.full_name from tbl_usersinfo u) tu ON tu.user_id = w.user_id where workspace_id = '$workspace_id' ORDER BY user_level_id ASC");

    if(mysql_num_rows($result)>0){

        $response["ws_members"] = array();

        $c = 1;
        while($row = mysql_fetch_array($result)){
            
            if ($c==1){
                $response["workspace_creator"] = $row["user_id"];
                $c=2;
            }
            $workspace = array();
            $workspace["user_id"] = $row["user_id"];
            $workspace["full_name"] = $row["full_name"];
            
            
            array_push($response["ws_members"], $workspace);

            

        }
        $response["success"]=1;
        echo json_encode($response);
    }else{
        $response["success"] = 0;
        $response["message"] = "No members on this workspace";
        echo json_encode($response);
    }

    
 

}
?>