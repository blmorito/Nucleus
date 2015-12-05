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
        $project_name = $_POST["project_name"];
        $project_desc = $_POST["project_desc"];
        $project_leader_id = $_POST["project_leader_id"];
        $deadline = $_POST["deadline"];

        mysql_query("START TRANSACTION");

        $result = mysql_query("UPDATE projects set project_name = '$project_name', project_desc = '$project_desc', project_deadline = '$deadline' where project_id = '$project_id'");
        $result1 = mysql_query("UPDATE projects_users set user_level_id  = '3' where user_level_id='1' and project_id = '$project_id'");
        $result2 = mysql_query("UPDATE projects_users set user_level_id = '1' where user_id = '$project_leader_id' and project_id = '$project_id'");


        if($result){
            mysql_query("COMMIT");
            $response["success"] = 1;
            $response["message"] = "Successfully updated project info";
            echo json_encode($response);
        }else{
            mysql_query("ROLLBACK");
            $response["success"] =0;
            $response["message"] = "Something went wrong";
            echo json_encode($response);
        }
       
        
     

    }
?>