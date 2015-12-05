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

    $project_id = $_POST['project_id'];
    $response["project_members"] = array();

    $queryz = mysql_query("SELECT p.user_id, tu.full_name FROM `projects_users`p LEFT JOIN (SELECT t.full_name, t.user_id from tbl_usersinfo t) tu ON tu.user_id = p.user_id where project_id = '$project_id' ORDER BY user_level_id ASC");
    $ld = 1;
    while($rowzz = mysql_fetch_array($queryz)){
        if($ld==1){
            $response["project_leader"] = $rowzz["user_id"]; 
            $ld+=1;
        }


        $pmem = array();
        $pmem["pmember_id"] = $rowzz["user_id"];
        $pmem["pmember_name"] = $rowzz["full_name"];

        array_push($response["project_members"], $pmem);

    }
   
   
    $project_id = $_POST["project_id"];

    $result = mysql_query("SELECT * FROM projects where project_id = '$project_id'");

    $row = mysql_fetch_array($result);

    if(mysql_num_rows($result)>0){
        $response["success"] = 1;
        $response["project_name"] = $row["project_name"];
        $response["project_desc"] = $row["project_desc"];
        $response["project_deadline"] = $row["project_deadline"];

        echo json_encode($response);
    }else{
        $response["success"] = 0;
        $response["message"] = "Something went horribly wrong";
        echo json_encode($response);
    }

    
 

}
?>