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

    $result = mysql_query("SELECT p.project_id, p.project_name, p.project_desc, tu.full_name from projects p LEFT JOIN (SELECT projects_users.project_id, projects_users.user_id, projects_users.user_level_id from projects_users) pu ON pu.project_id = p.project_id LEFT JOIN (SELECT tbl_usersinfo.user_id, tbl_usersinfo.full_name from tbl_usersinfo) tu on tu.user_id = pu.user_id where workspace_id = '$workspace_id' and pu.user_level_id=1");

    if(mysql_num_rows($result)>0){

        $response["projects"] = array();

        while($row = mysql_fetch_array($result)){
            

            $projects = array();
            $projects["project_id"] = $row["project_id"];
            $projects["project_name"] = $row["project_name"];
            $projects["project_desc"] = $row["project_desc"];
            $projects["project_leader"] = $row["full_name"];
            
            array_push($response["projects"], $projects);

            

        }
        $response["success"]=1;
        echo json_encode($response);
    }else{
        $response["success"] = 0;
        $response["message"] = "No projects yet on this workspace";
        echo json_encode($response);
    }

    
 

}
?>