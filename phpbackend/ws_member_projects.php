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
    $user_id = $_POST["user_id"];

    $result = mysql_query("SELECT p.project_id, p.project_name FROM ws_members w LEFT JOIN (SELECT projects.project_id, projects.project_name, projects.workspace_id from projects) p ON p.workspace_id = w.workspace_id LEFT JOIN (SELECT projects_users.project_id, projects_users.user_id, projects_users.user_level_id from projects_users) pu ON pu.project_id = p.project_id WHERE pu.user_id = '$user_id' and w.workspace_id = '$ws_id' and pu.user_level_id!=3 and w.user_level_id!=3") or trigger_error(mysql_error());

    if(mysql_num_rows($result)>0){

      
        $response["projects"] = array();
    	
        while($row = mysql_fetch_array($result)){
       		
       		$project = array();
           
            $project["project_id"] = $row["project_id"];
            $project["project_name"] = $row["project_name"];
            
			
			array_push($response["projects"], $project);
            
            

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