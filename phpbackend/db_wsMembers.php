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

    $result = mysql_query("SELECT w.workspace_id,w.user_id as user_id, em.email as email, (SELECT user_levels.user_level from user_levels where user_level_id = w.user_level_id) UserLevel, u.full_name as full_name, u.avatar_id, u.avatar_img as avatar_img from ws_members w LEFT JOIN (SELECT tbl_usersinfo.user_id, tbl_usersinfo.full_name,tbl_usersinfo.avatar_id, (SELECT avatars.avatar_img from avatars where avatar_id = tbl_usersinfo.avatar_id ) avatar_img from tbl_usersinfo) u ON u.user_id = w.user_id LEFT JOIN (SELECT tbl_users.user_id, tbl_users.email FROM tbl_users) em on em.user_id = w.user_id where w.workspace_id = '$ws_id' ORDER BY w.user_level_id") or trigger_error(mysql_error());

    if(mysql_num_rows($result)>0){

        $response["members"] = array();
    	
        while($row = mysql_fetch_array($result)){
       		
       		$member = array();
            $member["user_id"] = $row["user_id"];
            $member["full_name"] = $row["full_name"];
            $member["email"] = $row["email"];
            $member["user_level"] = $row["UserLevel"];
           	$member["avatar"] = base64_encode($row["avatar_img"]);
			
			array_push($response["members"], $member);

            

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