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

    $result = mysql_query("SELECT pu.user_id, tu.full_name, pu.date_joined, pu.user_level_id, av.avatar_img FROM `projects_users` pu LEFT JOIN (SELECT tx.user_id, tx.full_name, tx.avatar_id from tbl_usersinfo tx) as tu ON tu.user_id = pu.user_id LEFT JOIN (Select a.avatar_id, a.avatar_img from avatars a) av ON av.avatar_id = tu.avatar_id WHERE project_id = '$project_id' ORDER BY pu.user_level_id ASC") or trigger_error(mysql_error());

    if(mysql_num_rows($result)>0){

        $response["members"] = array();
    	
        while($row = mysql_fetch_array($result)){
       		
       		$member = array();
            $member["user_id"] = $row["user_id"];
            $member["full_name"] = $row["full_name"];

            $phpdate = strtotime($row["date_joined"]);
            $phpdatex = date( 'M d, Y', $phpdate );

            $member["date"] = "Joined last ".$phpdatex;

            if ($row["user_level_id"]==1){

                $member["role"] = "Leader";
            }elseif ($row["user_level_id"]==2) {

                $member["role"] = "Administrator";
            }else{

                $member["role"] = "Member";
            }
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