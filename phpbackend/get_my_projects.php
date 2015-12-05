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
    $user_id = $_POST["user_id"];

    $result = mysql_query("SELECT p.project_id as p_id, pp.project_name as p_name, pp.project_desc as p_desc, tu.full_name, (SELECT full_name from tbl_usersinfo where user_id = (SELECT user_id from projects_users where project_id = p.project_id and user_level_id = 1)) as leader FROM `projects_users` p LEFT JOIN (SELECT projects.project_id, projects.project_name, projects.project_desc, projects.workspace_id from projects) pp ON pp.project_id = p.project_id LEFT JOIN (SELECT tbl_usersinfo.user_id, tbl_usersinfo.full_name from tbl_usersinfo) tu ON tu.user_id = p.user_id WHERE p.user_id = '$user_id' and pp.workspace_id = '$workspace_id'");

    if(mysql_num_rows($result)>0){

        $response["projects"] = array();

        while($row = mysql_fetch_array($result)){
            

            $projects = array();
            $projects["project_id"] = $row["p_id"];
            $projects["project_name"] = $row["p_name"];
            $projects["project_desc"] = $row["p_desc"];
            $projects["project_leader"] = $row["leader"];
            
            array_push($response["projects"], $projects);
            
            

        }
        $response["success"]=1;
        echo json_encode($response);
    }else{
        $response["projects"] = array();

        while($row = mysql_fetch_array($result)){
            

            $projects = array();
            $projects["project_id"] = $row["p_id"];
            $projects["project_name"] = $row["p_name"];
            $projects["project_desc"] = $row["p_desc"];
            $projects["project_leader"] = $row["full_name"];
            array_push($response["projects"], $projects);
            
            

        }
        $response["success"]=1;
        echo json_encode($response);
    }

    
 

}
?>